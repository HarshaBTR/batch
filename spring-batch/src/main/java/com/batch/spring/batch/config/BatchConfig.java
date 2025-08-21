package com.batch.spring.batch.config;

import com.batch.spring.batch.entity.Customer;
import com.batch.spring.batch.repo.CustomerRepo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class BatchConfig {
    private StepBuilder stepBuilder;
    private JobBuilder jobBuilder;
    @Autowired
    private CustomerRepo customerRepo;

    public FlatFileItemReader<Customer>reader(){
        FlatFileItemReader itemReader=new FlatFileItemReader();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        itemReader.setName("csvreader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(linemapper());
        System.out.println("Reader is Sucessfull");
        return itemReader;
    }

    private LineMapper linemapper() {
        DefaultLineMapper<Customer> lineMapper=new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        System.out.println("Mapper is Sucessfull");

        return lineMapper;
    }

    @Bean
    public ItemProcessor<Customer,Customer> processor(){
        return new CustomerProcessor();
    }

  @Bean
    public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer>writer=new RepositoryItemWriter<>();
      System.out.println("Writter started");
      writer.setRepository(customerRepo);
        writer.setMethodName("save");
      System.out.println("Writter is Sucessfull");

      return writer;
  }
 @Bean
  public Step step1(JobRepository jobRepository, @Autowired PlatformTransactionManager transactionManager){
    return new StepBuilder("Csv-step",jobRepository)
            .<Customer,Customer>chunk(10,transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
  }

  @Bean
    public Job job(JobRepository jobRepository,Step step1){
        return new JobBuilder("job",jobRepository)
                .start(step1)
                .build();

  }
}
