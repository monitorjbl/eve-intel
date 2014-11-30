#!/bin/bash

# Install build tools and tomcat
apt-get update
apt-get install -y openjdk-7-jdk git maven tomcat7
apt-get install -y iptables-persistent
iptables-save > /etc/iptables/rules.v4

# Set up routing
iptables -A INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -i eth0 -p tcp --dport 8080 -j ACCEPT
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --t
o-port 8080

# Checkout code
cd /tmp
git clone https://github.com/monitorjbl/eve-intel.git
cd eve-intel

# Build and deploy
mvn clean package
cp target/eve-intel.war /var/lib/tomcat7/webapps/
