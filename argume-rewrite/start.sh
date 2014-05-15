#!/bin/sh
# Secret for DB password encryption
export DB_PWD_ENC_SECRET=s3cr3t 

# start with nohup
nohup java -DDB=rds-postgres -jar $JETTY_HOME/jetty-runner.jar \
			--out jetty-output.log --log jetty-requests.log jetty.xml \
			argume-rewrite.war > jetty-runner.log 2>&1 &