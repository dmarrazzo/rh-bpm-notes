# Red Hat Process Automation Manager and Red Hat Decision Manager Usage notes

This project collects my experiences on [Red Hat Process Automation Manager][1] and [Red Hat Decision Manager][2] (formerly known as JBoss BRMS).

There are documents and samples.

**Business Optimizer** is an important capabilities of *Red Hat Decision Manager*, find here a dedicated [page](business-optimizer.md).

**Disclaimer** 

- *Red Hat Process Automation Manager* was previously known as *Red Hat JBoss BPM Suite*. It is the productised version of the upstream project [jbpm](http://www.jbpm.org) 
- *Red Hat Decision Manager* was previously known as *Red Hat JBoss BRMS*. It is the productised version of the upstream projects [drools](http://www.drools.org) and [optaplanner](http://www.optaplanner.org) 



[1]: https://www.redhat.com/en/technologies/jboss-middleware/process-automation-manager
[2]: https://www.redhat.com/en/technologies/jboss-middleware/decision-manager

## Introduction and news

 - [Process management and business logic for responsive cloud-native applications: Red Hat Process Automation Manager is released](https://middlewareblog.redhat.com/2018/06/19/process-management-and-business-logic-for-responsive-cloud-native-applications-red-hat-process-automation-manager-is-released/)

 - [From BPM and business automation to digital automation platforms](https://middlewareblog.redhat.com/2018/07/18/from-bpm-and-business-automation-to-digital-automation-platforms/)

 - [Digital Automation Platforms: Injecting speed into application development](https://middlewareblog.redhat.com/2018/06/06/digital-automation-platforms-injecting-speed-into-application-development/)

## Other source of information on Red Hat JBoss BPM

### Blogs

- [The Ultimate Guide to Business Central and Git](http://porcelli.me/rhba/business-central/git/2018/11/05/business-central-git.html)

- [Reducing data inconsistencies with Red Hat Process Automation Manager](https://developers.redhat.com/blog/2018/08/22/reducing-data-inconsistencies-with-red-hat-process-automation-manager/)

- [Demystifying the Business Central repository](http://www.opensourcerers.org/demystifying-business-central-repository/)

- [Using loosely coupled rules in your process](http://www.opensourcerers.org/loose-coupled-rules/)

- [Capture your decisions with DMN](http://www.opensourcerers.org/capture-your-decisions-with-dmn/)

- [Extend Red Hat JBoss BPM Suite through the Service Repository](https://developers.redhat.com/blog/2018/01/30/red-hat-jboss-bpm-suite/)

- [Unit-testing your BPM processes by bending time](https://developers.redhat.com/blog/2016/07/18/unit-testing-your-bpm-processes-by-bending-time/)
 
- [What is a kjar?](https://developers.redhat.com/blog/2018/03/14/what-is-a-kjar/)

- [Business process management in a microservices world](https://developers.redhat.com/blog/2016/10/10/business-process-management-in-a-microservices-world/)

- [Deep dive on case management](https://rh2017.smarteventscloud.com/connect/sessionDetail.ww?SESSION_ID=104878)

- [Effective Case Management within a BPM Framework](https://middlewareblog.redhat.com/2018/06/19/effective-case-management-within-a-bpm-framework/)

- [Using sidecars to analyze and debug network traffic in OpenShift and Kubernetes pods](https://developers.redhat.com/blog/2019/02/27/sidecars-analyze-debug-network-traffic-kubernetes-pod/)

- [Demo monitoring PAM](https://github.com/jbossdemocentral/rhpam7-monitoring-addon)

- [Detecting credit card fraud with Red Hat Decision Manager 7](https://developers.redhat.com/blog/2018/07/26/detecting-credit-card-fraud-with-red-hat-decision-manager-7/)

### Video and Webinars

 - [Understand the kieserver REST APIs, monitoring the Java APIs traffic](https://youtu.be/v7Td4PsT1O8)

 - [Webinar on simulation - BPMSim](https://www.youtube.com/watch?v=xNzM7A3MGJI&list=PLZPWJhPaP-K7u2cjmyhf2SknXX9HhyWrq)

 - [Standards in Business Rules Space: Decision Model and Notation (DMN) and Other Standards](https://youtu.be/fXYD_HE7ufc)

 - [Red Hat Business Automation primer: Vision and roadmap](https://youtu.be/oQCkA_HzYoU)

 - [Building Business Applications with DMN and BPMN](https://youtu.be/C0u3ZDiH3ek)

 - [Monitoring Prometheus Graphana Rules DMN- 7.4](https://youtu.be/huKP9KhCD2k)

 - [Monitoring Prometheus Graphana Optaplanner- 7.4](https://youtu.be/q0mSR36Xnmkk)

### Books

 - **Free ebook** [Effective Business Process Management with JBoss BPM](https://developers.redhat.com/books/effective-business-process-management-jboss-bpm/)
 - [Mastering jBPM 6](https://www.packtpub.com/application-development/mastering-jbpm6)
 
### Official courses

 - [Red Hat Decision Manager and Red Hat Process Automation Manager 7.0 for Business Users - JB371](https://www.redhat.com/en/services/training/jb371-red-hat-decision-manager-and-red-hat-process-automation-manager-70-business-users)
 - [Red Hat Decision Manager and Red Hat Process Automation Manager 7.0 for Developers - JB373](https://www.redhat.com/en/services/training/jb373-red-hat-decision-manager-and-red-hat-process-automation-manager-70-developers)