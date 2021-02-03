package com.example.cookiecutter

import com.sksamuel.avro4s.BinaryFormat
import com.sksamuel.avro4s.kafka.GenericSerde
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.avro.Schema
import org.apache.avro.reflect.ReflectData
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

import java.util.Properties
import scala.jdk.CollectionConverters._

class CustomPartitionerSpec extends SpecBase {

  val topic = "customPartitionerTopic"
  val setup: LocalSetup = LocalSetup()

  val commonProps = new Properties()
  commonProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, setup.adminClientBootstrapServer)

  private val serde = new GenericSerde[CookieBox](BinaryFormat)

  val producerProperties: Properties = commonProps.clone().asInstanceOf[Properties]
  producerProperties.put(ProducerConfig.ACKS_CONFIG, "all")
  producerProperties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
  producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "customPartitionerProducer")
  producerProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, classOf[CookieFlavorPartitioner])
  producerProperties.put(CookieFlavorPartitioner.COOKIE_FLAVOR_CONFIG , "vanilla")
  producerProperties.put(CookieFlavorPartitioner.VERBOSE_CONFIG , true)
  val producer: Producer[String, CookieBox] =
    new KafkaProducer[String, CookieBox](producerProperties, new StringSerializer(), serde.serializer())

  val consumerProps: Properties = commonProps.clone().asInstanceOf[Properties]
  consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "customPartitionerConsumer")
  consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  val consumer = new KafkaConsumer[String, CookieBox](consumerProps, new StringDeserializer, serde.deserializer())

  override def beforeAll(): Unit = {
    SpecHelper.deleteTopic(topic, setup.adminClient)
    SpecHelper.createTopic(topic, setup.adminClient, numberOfPartitions = 6)
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    producer.close()
    consumer.close()
    super.afterAll()
  }

  "use custom partitioner" in {

    val jaffaCakes                         = CookieBox("batch1", "orange", 6 )
    val record1 = new ProducerRecord[String, CookieBox](topic, jaffaCakes.batchId, jaffaCakes)
    val moreJaffaCakes                         = CookieBox("batch2", "orange", 12 )
    val record2 = new ProducerRecord[String, CookieBox](topic, moreJaffaCakes.batchId, moreJaffaCakes)
    val madagascarDelight                         = CookieBox("batch1", "vanilla", 4 )
    val record3 = new ProducerRecord[String, CookieBox](topic, madagascarDelight.batchId, madagascarDelight)
    producer.send(record1).get()
    producer.send(record2).get()
    producer.send(record3).get()

    consumer.subscribe(List(topic).asJava)

    SpecHelper.fetchAndPrintRecords(consumer)
  }

  "generate schema" in {
    val schema: Schema = ReflectData.get().getSchema(classOf[CookieBox])
    println(schema)
  }
}
