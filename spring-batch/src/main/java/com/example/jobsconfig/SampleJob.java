package com.example.jobsconfig;

import com.example.listener.SkipListener;
import com.example.model.*;
import com.example.service.StudentService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.listener.FirstJobListener;
import com.example.listener.FirstStepListener;
import com.example.processor.FirstItemProcessor;
import com.example.reader.FirstItemReader;
import com.example.service.SecondTasklet;
import com.example.writer.FirstItemWriter;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SecondTasklet secondTasklet;
	
	@Autowired
	private FirstJobListener firstJobListener;
	
	@Autowired
	private FirstStepListener firstStepListener;
	
	@Autowired
	private FirstItemReader firstItemReader;
	
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	
	@Autowired
	private FirstItemWriter firstItemWriter;
	@Autowired
	private StudentService studentService;
	@Autowired
	private SkipListener skipListener;
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.seconddatasource")
	public DataSource seconddatasource() {
		return DataSourceBuilder.create().build();
	}

	// tasklet job
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
				.incrementer(new RunIdIncrementer())
				.start(firstStep())
				.next(secondStep())
				.listener(firstJobListener) // interceptor
				.build();
	}

	private Step firstStep() {
		return stepBuilderFactory.get("First Step")
				.tasklet(firstTask())
				.listener(firstStepListener)
				.build();
	}

	private Tasklet firstTask() {
		return (contribution, chunkContext) -> {
			System.out.println("This is first tasklet step");
			System.out.println("SEC = " + chunkContext.getStepContext().getStepExecutionContext());
			return RepeatStatus.FINISHED;
		};
	}
	
	private Step secondStep() {
		return stepBuilderFactory.get("Second Step")
				.tasklet(secondTasklet)
				.build();
	}
	
	/*
	 * private Tasklet secondTask() { return new Tasklet() {
	 * 
	 * @Override public RepeatStatus execute(StepContribution contribution,
	 * ChunkContext chunkContext) throws Exception {
	 * System.out.println("This is second tasklet step"); return
	 * RepeatStatus.FINISHED; } }; }
	 */

	// chunk job
	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}
	
	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentResponse, StudentResponse>chunk(4)
				//.reader(flatFileItemReader(null))
				//.reader(jsonItemReader(null))
				//.reader(staxEventItemReader(null))
				//.reader(jdbcCursorItemReader())
				.reader(itemReaderAdapter())
//				.processor(firstItemProcessor)
				.writer(firstItemWriter)
				.faultTolerant()
				.skip(FlatFileParseException.class)
				.skip(NullPointerException.class)
//				.skipLimit(Integer.MAX_VALUE)
//				.skipPolicy(new AlwaysSkipItemSkipPolicy())
				.retryLimit(3)
				.retry(Throwable.class)
				.listener(skipListener)
				.build();
	}

	// csv reader
	@StepScope
	@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemReader<StudentCsv> flatFileItemReader =
				new FlatFileItemReader<StudentCsv>();

		flatFileItemReader.setResource(fileSystemResource);

		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");
					}
				});

				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				});

			}
		});

		/*
		DefaultLineMapper<StudentCsv> defaultLineMapper =
				new DefaultLineMapper<StudentCsv>();

		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper =
				new BeanWrapperFieldSetMapper<StudentCsv>();
		fieldSetMapper.setTargetType(StudentCsv.class);

		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		flatFileItemReader.setLineMapper(defaultLineMapper);
		*/

		flatFileItemReader.setLinesToSkip(1);

		return flatFileItemReader;
	}
	// json reader
	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		JsonItemReader<StudentJson> jsonItemReader =
				new JsonItemReader<StudentJson>();

		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(
				new JacksonJsonObjectReader<>(StudentJson.class));

		jsonItemReader.setMaxItemCount(8);
		jsonItemReader.setCurrentItemCount(2);

		return jsonItemReader;
	}

	// xml reader
	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		StaxEventItemReader<StudentXml> staxEventItemReader =
				new StaxEventItemReader<StudentXml>();

		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});

		return staxEventItemReader;
	}
	// jdbc reader
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader =
				new JdbcCursorItemReader<StudentJdbc>();

		jdbcCursorItemReader.setDataSource(seconddatasource());
		jdbcCursorItemReader.setSql(
				"select id, first_name as firstName, last_name as lastName,"
						+ "email from student");

		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
			{
				setMappedClass(StudentJdbc.class);
			}
		});

		//jdbcCursorItemReader.setCurrentItemCount(2);
		//jdbcCursorItemReader.setMaxItemCount(8);

		return jdbcCursorItemReader;
	}
	// rest api reader
	public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
		ItemReaderAdapter<StudentResponse> itemReaderAdapter =
				new ItemReaderAdapter<StudentResponse>();

		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("getStudent");
		itemReaderAdapter.setArguments(new Object[] {1L, "Test"});

		return itemReaderAdapter;
	}
	@StepScope
	@Bean
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter =
				new FlatFileItemWriter<StudentJdbc>();

		flatFileItemWriter.setResource(fileSystemResource);

		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name,Last Name,Email");
			}
		});

		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			{
				//setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] {"id", "firstName", "lastName", "email"});
					}
				});
			}
		});

		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
			@Override
			public void writeFooter(Writer writer) throws IOException {
				writer.write("Created @ " + new Date());
			}
		});

		return flatFileItemWriter;
	}

	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		JsonFileItemWriter<StudentJson> jsonFileItemWriter =
				new JsonFileItemWriter<>(fileSystemResource,
						new JacksonJsonObjectMarshaller<StudentJson>());

		return jsonFileItemWriter;
	}

	@StepScope
	@Bean
	public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		StaxEventItemWriter<StudentJdbc> staxEventItemWriter =
				new StaxEventItemWriter<StudentJdbc>();

		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("students");

		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentJdbc.class);
			}
		});

		return staxEventItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter =
				new JdbcBatchItemWriter<StudentCsv>();

		jdbcBatchItemWriter.setDataSource(seconddatasource());
		jdbcBatchItemWriter.setSql(
				"insert into student(id, first_name, last_name, email) "
						+ "values (:id, :firstName, :lastName, :email)");

		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());

		return jdbcBatchItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriterWithPreparedStmt() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter =
				new JdbcBatchItemWriter<StudentCsv>();

		jdbcBatchItemWriter.setDataSource(seconddatasource());
		jdbcBatchItemWriter.setSql(
				"insert into student(id, first_name, last_name, email) "
						+ "values (?,?,?,?)");

		jdbcBatchItemWriter.setItemPreparedStatementSetter(
				new ItemPreparedStatementSetter<StudentCsv>() {

					@Override
					public void setValues(StudentCsv item, PreparedStatement ps) throws SQLException {
						ps.setLong(1, item.getId());
						ps.setString(2, item.getFirstName());
						ps.setString(3, item.getLastName());
						ps.setString(4, item.getEmail());
					}
				});

		return jdbcBatchItemWriter;
	}

	public ItemWriterAdapter<StudentCsv> itemWriterAdapter() {
		ItemWriterAdapter<StudentCsv> itemWriterAdapter =
				new ItemWriterAdapter<StudentCsv>();

		itemWriterAdapter.setTargetObject(studentService);
		itemWriterAdapter.setTargetMethod("restCallToCreateStudent");

		return itemWriterAdapter;
	}
}
