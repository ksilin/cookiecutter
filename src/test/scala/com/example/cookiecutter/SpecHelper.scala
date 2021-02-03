/*
 * Copyright 2021 ksilin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cookiecutter

import java.util.Collections
import org.apache.kafka.clients.admin.{AdminClient, CreateTopicsResult, NewTopic}
import org.apache.kafka.clients.consumer.{Consumer, ConsumerRecords}
import org.apache.kafka.common.config.TopicConfig
import wvlet.log.LogSupport

import java.time
import scala.jdk.CollectionConverters._

object SpecHelper extends LogSupport {

  def createTopic(
      topicName: String,
      adminClient: AdminClient,
      numberOfPartitions: Int = 1,
      replicationFactor: Short = 1
  ): Unit =
    if (!adminClient.listTopics().names().get().contains(topicName)) {
      debug(s"Creating topic ${topicName}")

      val configs: Map[String, String] =
        if (replicationFactor < 3) Map(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG -> "1")
        else Map.empty

      val newTopic: NewTopic = new NewTopic(topicName, numberOfPartitions, replicationFactor)
      newTopic.configs(configs.asJava)
      try {
        val topicsCreationResult: CreateTopicsResult =
          adminClient.createTopics(Collections.singleton(newTopic))
        topicsCreationResult.all().get()
      } catch {
        case e: Throwable => debug(e)
      }
    } else {
      info(s"topic $topicName already exists, skipping")
    }

  def deleteTopic(topicName: String, adminClient: AdminClient): Any = {
    debug(s"deleting topic $topicName")
    try {
      val topicDeletionResult = adminClient.deleteTopics(List(topicName).asJava)
      topicDeletionResult.all().get()
    } catch {
      case e: Throwable => debug(e)
    }
  }

  def fetchAndPrintRecords[K, V](consumer: Consumer[K, V]): Unit = {
    val duration: time.Duration = java.time.Duration.ofMillis(100)
    var found                   = false
    var attempts                = 0
    val maxAttempts             = 100
    while (!found && attempts < maxAttempts) {
      val records: ConsumerRecords[K, V] = consumer.poll(duration)

      attempts = attempts + 1
      found = !records.isEmpty
      if (found) {
        info(s"fetched ${records.count()} records on attempt $attempts")
        records.asScala foreach { r =>
          info(s"${r.topic()} | ${r.partition()} | ${r.offset()}: ${r.key()} | ${r.value()}")
        }
      }
    }
  }


}
