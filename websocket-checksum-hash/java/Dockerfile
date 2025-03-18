FROM maven:3.8-openjdk-11

WORKDIR /app

COPY . .

# Build the project with Maven and ensure dependencies are copied
RUN mvn clean package dependency:copy-dependencies

# Run the test class directly with java command
# Use the full classpath including target/classes
CMD ["java", "-cp", "target/classes:target/dependency/*", "com.ripio.trade.ChecksumHashTest"]
