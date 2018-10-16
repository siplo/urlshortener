# urlshortener
Weblfux a reactive example and reactive programming. 

There are two functional controller and two annotated controller. 
**Functional controller**  
First one provide methods for manipulate with url - standard CRUD operation. 
Second one does redirecting to original url.

**Annotated controller**  

Same concept like above. Annotated controller are much familiar for people who use spring mvc. 

**Services:**

There are 3 DAO service - jpa, jdbc (rxjava) and redis. Database is h2. Because url must survive restart of application, h2 store data to file.

Redis - redis has reactive support via letuce driver.  
rx-java - provide reactive wrapper on standard JDBC blocking operation. It's kind of oldschool sql programing. But there are no so much no blocking jdbc driver.

**Implementation:**


 Services are not in "production mode". Reason for that is I want to tried as much of reactive approach as possible. For instance mark url as not valid 
 is done by retrieve all url and then filter all which are older then defined time. It could be done by one update command. 
 
  
**What I have to improve:**

1)Error handling in Webflux and also in reactive world :) 
2) Junit 4/5 - I had real problem with IDE (idea) and Webfluxtest client with Junit5 version. I decied to use Junit4. WebluxClient and functional endpoint also doesn't work as I expected. Look to documentation (not supported).
