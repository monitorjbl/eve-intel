# EVE Intel

This is an intel tool for quickly gathering information about pilots. It is designed to present a brief summary of the pilot's recent kill history including the types of ships most favored, most commonly targeted alliance, and the pilot's most active region.

This tool sources its data from [zKillboard's API](https://zkillboard.com/) and the EVE API. As such, it is dependent on the uptime of these systems; if zKill goes down or becomes slow, EVE Intel may become unavailable.

You can access a running instance of the application here: http://monitorjbl.io/eve-intel/.

# Stats Information

Most stats calculated are pretty self-explanatory. Some of the less obvious ones are listed below:

* Average fleet size: This is determined by taking the average count of assists on each killmail.
* Most assisted alliance: This is determined by checking all of the pilots assisting any kills on the pilot's killboard.
* Most flown ship: This is determined by checking all of the ships flown by the pilot on all of his kills.
* Cyno pilot: If there are more than 2 losses on the pilot's killboard where a cyno was fitted to his ship, he is flagged.
* Fleet booster: If there is 1 or more losses on the pilot's killboard where a fleet assist module was fitted to his ship, he is flagged.

# History and Technical Stuff

I originally wrote this tool to give myself some heads-up info on pilots in local while I moved around in null sec. It was a pretty simple Spring-based standalone application with an AngularJS-based frontend and EHcache to keep things speedy. After hearing about [Amazon's Lambda](https://aws.amazon.com/lambda/) service, I wanted to see how cheaply I could run a site.

To start with, I gutted the Spring-based stuff since Lambdas are effectively just running `java -jar` on your code. The only reason I was using it was for the MVC stuff, and the amount of dependency injection I needed to do was pretty minimal. Since [Amazon's API Gateway](https://aws.amazon.com/api-gateway/) offered integrations with the Lambda service, I figured I could just convert the MVC controllers and call it a day.

Unfortunately, one of the things they don't mention very clearly is that the API Gateway has a hard timeout of 10 seconds on any execution of a Lambda function. Lambdas themselves have a timeout cap of 60 seconds, which is more than enough for most requests but I wanted to make sure the app could handle large requests as well.

This required a total rethink of how the app handled requests. Lambdas are able to listen to S3 events, which would allow the function to run for longer than 10 seconds if needed. Rather than have a single RESTful endpoint to make requests and get data, there are two endpoints. The first is an Amazon API Gateway endpoint to submit a load request. This happens very quickly, as it simply stores the requested pilots in S3 (after first checking to see if their data already exists and is current) as a JSON list. When the file is saved in S3, a Lambda function is triggered to load the pilots' data and store the result in a separate file.

The second part was what required the most reconsideration. The frontend now has to go out and get each pilot's data straight from S3. However, as there is not a guarantee that it will be present, the UI had to be heavily updated to wait and retry fetch and record that failed. This turned out to be fairly complex, but in the end it turned out pretty well I think!

The overall effect that this has from a user's standpoint is that the browser has to do more work and make many more requests. For typical datasets (5-10 pilots), this is not really noticeable on any device and in fact is somewhat improved over the original experience because pilots load individually instead of all at once. 

For larger datasets, network I/O starts to be a bit of a bottleneck; if you are loading 100 pilots, your browser now makes 100 separate requests instead of one. From start to finish, this takes longer to complete but since individual pilots start rolling in as soon as they're ready the overall experience seems better.

# Hosting Price

But what about price? Well, let's do math:

| Service     | Cost                                                       |
| ------------|------------------------------------------------------------|
| Lambda      | $0.20 per 1 million requests + $0.00001667 per GB-sec used |
| S3 Storage  | $0.03 per gigabyte                                         |
| S3 Reads    | $0.40 per 1 million requests                               |
| S3 Writes   | $5.00 per 1 million requests                               |
| API Gateway | $3.50 per 1 million requests                               |

That's a lot of numbers, so to clarify here's what a basic request for 15 uncached pilots causes:

1. 1 call to request API: **$0.0000035**
2. 1 Lambda invocation for request, runs for average of 5 seconds at 256MB: $0.0000002 + .25 * $0.00001667 * 5 = **$0.00008355**
3. 1 write to S3 to request load: **$0.000005**
4. 2 Lambda invocations for load, run for an average of 15 seconds at 1024MB: $0.0000004 + $0.00001667 * 20 = **$0.0005005**
5. 15 writes to S3: **$0.000075**
6. 15 reads from S3 every 3 seconds over 20 seconds, tapering off over time. Let's say 55 reads: **$0.000022**
7. Total S3 storage is roughly 200KB if they are active pilots: 0.0002 * 0.3 = **0.0000006**

The grand total for this request is: **$0.00069015**. I could make 7244 identical requests for about 5 bucks. However, that's not counting the cached case. What if the pilots requests don't need to be loaded because they exist and are recent? Well, you can skip steps 3, 4, 5, most of 6, and 7!

1. 1 call to request API: **$0.0000035**
2. 1 Lambda invocation for request, runs for average of 5 seconds at 256MB: $0.0000002 + .25 * $0.00001667 * 5 = **$0.00008355**
3. 15 reads from S3: **$0.000006**

The grand total for this request? **$0.00009305**. 53,734 of these requests could be made for about 5 bucks. Assuming a healthy mix of cached requests, this service could be theoretically for dozens or hundreds of people for about the price of a Taco Bell meal a month. It's actually even cheaper than that since Amazon doesn't bill for the first 1 million Lambda requests and up to 400,000 GB-seconds!

Lambdas are definitely an interesting way to build a site. The design pattern required for the backend really starts to affect how you build the frontend, which is somewhat problematic for many use cases. But considering that this application could be run for a handful of chalupas at a usage level that I would consider absurdly optimistic for this app, it's a very compelling option if you can get away with it.
