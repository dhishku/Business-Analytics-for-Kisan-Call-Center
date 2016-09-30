# Business Analytics for Kisan Call Center
## What is Kisan Call Center
[Kisan Call Center](http://mkisan.gov.in/KCC/KCCDashboard.aspx) also known as KCC, is a toll free call center service being run by the government where farmers can call and ask their queries. Agriculture garduates and experts man these call centers. Spread across the country, on an average it receives about 500,000 calls per month making it very useful for any analysis.

## Project Objective
Our objective is to derive actionable business intelligence from analysing the KCC data. These are calls directly from the farmers so have the potential of giving us a reliable and timely feedback from the field. So for instance, we can know practically on a real-time basis from the data that incidences of XX pest are increasing for CC crop in SS state. This makes it very useful and the administrative machinery can be activated, ncesssary pesticides can be moved into that area in a timely fashion and further distress can be reduced. It can also serve as a useful tool for us to gain insights into a particular phenomenon. For example, it should be able to tell us that this year, distress among farmers in say Maharashtra was particularly high due to this factor, and then corrective actions may be undertaken. Another possible use case is in using it as a feedback mechanism for various initiatives of the department. So for example, if this year suddenly a larger number of farmers are asking about soil health card, then it confirms that things are moving on well on ground. If after a pest attack, we move pesticides into the area, but still the calls complaining about the pest keep on increasing, it may indicate that something went wrong and the pesticide is not reaching the farmers. 

These are just some of the use cases, the possibilities are infinite.

## Project Structure

1. The project uses mysql data to store the database. The schema of the KCCDATA table in MYSQL testDB database is as given below.
`
+------------+--------------+------+-----+---------+----------------+
| Field      | Type         | Null | Key | Default | Extra          |
+------------+--------------+------+-----+---------+----------------+
| call_id    | int(11)      | NO   | PRI | NULL    | auto_increment |
| location   | varchar(25)  | NO   |     | NULL    |                |
| crop       | varchar(100) | YES  |     | NULL    |                |
| annotation | varchar(100) | YES  |     | NULL    |                |
| date       | date         | YES  |     | NULL    |                |
+------------+--------------+------+-----+---------+----------------+`

2. The project uses a RESTFUL API structure and is run on Tomcat v7 server. 
3. To run the project, clone the repository, open in eclipse as a gradle project. Run the task "gradle eclipseWTP" to build the WAR file. Then Run as --> 1. Run on Server... Select the Tomcat v7 server.