# COVID Tracker

Simple app showing world´s latest confirmed COVID cases, updated on a github repository that contains (or should) this information. The project real purpouse was for me to practice some Java with Spring and Thymeleaf.

Although i´ve followed an *youtube tutorial* there are some differences on my code. I use *JAVA 8* instead of *JAVA 12* used on the tutorial, and also i had to import the *HttpClient* lib in my *pom.xml*, as you can see on the external links i´ve quote on the external links.

* The original package name 'io.jbqneto.covid-tracker' is invalid and this project uses 'io.jbqneto.covidtracker' instead.

## Branch

This branch contains what the tutorial proposed to do, with some small changes. Latter on *MASTER* more things will be implemented.

## Technology used
| Tech | Use |
| ------ | ------ |
| Java | Language used to develop the project  |
| Spring | Java Framework chosen  |
| Thymeleaf | Used to show HTML content with the recovered data  |
| Apache HttpClient | Used to call http GET on github page  |  
| Apache CSV Commons | Used to read data in a csv and pass it to a JAVA Bean  |  

### External URL´s
* [youtube tutorial](https://www.youtube.com/watch?v=8hjNG9GZGnQ) - Java Brains video tutorial i followed for this app.
* [HttpClient](https://mkyong.com/java/apache-httpclient-examples/) - Apache HttpClient GET example.
* [CSV Commons](https://commons.apache.org/proper/commons-csv/) - Apache CSV Commons library also containg some examples of how to use it.
* [SSEGISandData](https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data/csse_covid_19_time_series) - Github repo from where i´ve readed the raw file containing the COVID data.

