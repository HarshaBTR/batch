package com.batch.spring.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @PostMapping("/importCustomer")
    public void importCsvToDB(){
        JobParameters jobParameters=new JobParametersBuilder().addLong("StartAt",System.currentTimeMillis()).toJobParameters();
        try{
            jobLauncher.run(job,jobParameters);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
