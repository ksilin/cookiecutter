package com.example.cookiecutter

import java.util
import org.apache.kafka.common.InvalidRecordException
import org.apache.kafka.common.utils.Utils
import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.common.Cluster
import wvlet.log.LogSupport

import java.nio.charset.Charset

class CookieFlavorPartitioner extends Partitioner with LogSupport {

  var flavor: String = CookieFlavorPartitioner.PLAIN_FLAVOR
  var verbose        = false

  override def configure(configs: util.Map[String, _]): Unit = {
    if (configs.containsKey(CookieFlavorPartitioner.VERBOSE_CONFIG)) {
      verbose = configs.get(CookieFlavorPartitioner.VERBOSE_CONFIG).asInstanceOf[Boolean]
      if (verbose) info("Hi, I will be your verbose partitioner today.")
    }
    if (configs.containsKey(CookieFlavorPartitioner.COOKIE_FLAVOR_CONFIG)) {
      flavor = configs.get(CookieFlavorPartitioner.COOKIE_FLAVOR_CONFIG).asInstanceOf[String]
      if (verbose) info(s"I like $flavor cookies too!")
    }
  }

  override def partition(
      topic: String,
      key: Any,
      keyBytes: Array[Byte],
      value: Any,
      valueBytes: Array[Byte],
      cluster: Cluster
  ): Int = {

    val partitions    = cluster.partitionsForTopic(topic)
    val numPartitions = partitions.size

    if (verbose) logParams(topic, key, value, cluster, numPartitions)

    val cookieBox = value.asInstanceOf[CookieBox]
    if (cookieBox.flavor == null || cookieBox.flavor.isEmpty)
      throw new InvalidRecordException("No flavorless cookies allowed")

    val partition =
      if (
        cookieBox.flavor.equalsIgnoreCase(flavor) ||
          cookieBox.flavor.equalsIgnoreCase(CookieFlavorPartitioner.SECRET_FLAVOR)
      ) {
        if (verbose) info(s"mmh, putting your favorite flavor into my favorite partition")
        0
      } else {
        val hash =
          Utils.toPositive(Utils.murmur2(cookieBox.flavor.getBytes(Charset.forName("UTF-8"))))
        hash % partitions.size
      }
    if (verbose) info(s"assigned partition: $partition")
    partition
  }

  private def logParams(
      topic: String,
      key: Any,
      value: Any,
      cluster: Cluster,
      numPartitions: Int
  ): Unit = {
    debug("partitioning for")
    debug(s"cluster: $cluster")
    debug(s"topic $topic with $numPartitions partitions")
    info(s"key: $key")
    info(s"value: $value")
  }

  override def close(): Unit = {}
}

object CookieFlavorPartitioner {
  val COOKIE_FLAVOR_CONFIG  = "me.want.cookie"
  private val PLAIN_FLAVOR  = "plain"
  private val SECRET_FLAVOR = "banana"
  val VERBOSE_CONFIG        = "partitioner.verbose"
}
