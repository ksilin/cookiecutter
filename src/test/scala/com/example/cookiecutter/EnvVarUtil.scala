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

object EnvVarUtil {

  // generates a warning:
  // WARNING: Illegal reflective access by com.example.ksqldb.EnvVarUtil$ (file:/home/ksilin/code/workspaces/confluent/KSQL/ksqldb_client/target/scala-2.13/test-classes/) to field java.util.Collections$UnmodifiableMap.m
  // looks like the only way around it is to use --add-opens
  def setEnv(key: String, value: String): Unit =
    try {
      val env   = System.getenv
      val cl    = env.getClass
      val field = cl.getDeclaredField("m")
      field.setAccessible(true)
      val writableEnv = field.get(env).asInstanceOf[java.util.Map[String, String]]
      writableEnv.put(key, value)
    } catch {
      case e: Exception =>
        throw new IllegalStateException("Failed to set environment variable", e)
    }

}
