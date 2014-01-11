SF Movies Notes
===============
1. Build it by **mvn clean package**

2. Run the back end by dropping target/sf-movies.war to any application server 
   
    e.g. 
        **java -jar jetty-runner.jar --port 9000 target/argume.war**

   ** This will allow Ajax requests from all hosts. Set a system property or environment variable called 'ALLOW_ORIGIN' to limit this behavior
    
    e.g. 
        **java -DALLOW_ORIGIN=http://localhost:8000 -jar jetty-runner.jar --port 9000 target/argume.war**

3. Run the front end by adding the 'static' directory to any web server.
    
    e.g. 
        **cd static** and then **python -m SimpleHTTPServer**

4. Go to http://localhost:8000