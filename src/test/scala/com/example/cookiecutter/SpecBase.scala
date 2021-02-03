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

import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import wvlet.log.{ LogLevel, LogSupport, Logger }

import java.util.Properties

class SpecBase
    extends AnyFreeSpec
    with Matchers
    with BeforeAndAfterAll
    with FutureConverter
    with LogSupport {

  EnvVarUtil.setEnv("RANDOM_DATA_GENERATOR_SEED", "9153932137467828920")
  Logger.setDefaultLogLevel(LogLevel.DEBUG)
  val loglevelProps = new Properties()
  loglevelProps.setProperty("org.apache.kafka", LogLevel.WARN.name)
  //Logger.setLogLevels(loglevelProps)

}
