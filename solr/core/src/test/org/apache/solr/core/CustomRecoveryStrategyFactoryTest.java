/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.core;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.cloud.RecoveryStrategy;
import org.apache.solr.cloud.RecoveryStrategyFactory;
import org.junit.BeforeClass;

/**
 * test that configs can override the RecoveryStrategyFactory
 */
public class CustomRecoveryStrategyFactoryTest extends SolrTestCaseJ4 {

  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig-customrecoverystrategyfactory.xml", "schema.xml");
  }

  public void testFactory() throws Exception {
    final RecoveryStrategyFactory recoveryStrategyFactory =
        h.getCore().getSolrCoreState().getRecoveryStrategyFactory();
    assertNotNull("recoveryStrategyFactory is null", recoveryStrategyFactory);
    assertEquals("recoveryStrategyFactory is wrong class (name)",
                 CustomRecoveryStrategyFactoryTest.CustomRecoveryStrategyFactory.class.getName(),
                 recoveryStrategyFactory.getClass().getName());
    assertTrue("recoveryStrategyFactory is wrong class (instanceof)",
        recoveryStrategyFactory instanceof CustomRecoveryStrategyFactory);
    final CustomRecoveryStrategyFactory customRecoveryStrategyFactory =
        (CustomRecoveryStrategyFactory)recoveryStrategyFactory;
}

  public void testCreate() throws Exception {
    final RecoveryStrategyFactory recoveryStrategyFactory =
        h.getCore().getSolrCoreState().getRecoveryStrategyFactory();
    assertNotNull("recoveryStrategyFactory is null", recoveryStrategyFactory);

    final RecoveryStrategy recoveryStrategy =
        recoveryStrategyFactory.create(null, null, null);

    assertEquals("recoveryStrategy is wrong class (name)",
                 CustomRecoveryStrategyFactoryTest.CustomRecoveryStrategy.class.getName(),
                 recoveryStrategy.getClass().getName());
    assertTrue("recoveryStrategy is wrong class (instanceof)",
        recoveryStrategy instanceof CustomRecoveryStrategy);

    final CustomRecoveryStrategy customRecoveryStrategy =
        (CustomRecoveryStrategy)recoveryStrategy;
    assertEquals(42, customRecoveryStrategy.getCustomParameter());
}

  static public class CustomRecoveryStrategy extends RecoveryStrategy {
    public CustomRecoveryStrategy(CoreContainer cc, CoreDescriptor cd,
        RecoveryListener recoveryListener) {
    }
    private int customParameter = random().nextInt();
    public int getCustomParameter() { return customParameter; }
    public void setCustomParameter(int customParameter) { this.customParameter = customParameter; }
    @Override
    public boolean getRecoveringAfterStartup() { return false; }
    @Override
    public void setRecoveringAfterStartup(boolean recoveringAfterStartup) {}
    @Override
    public void close() {}
  }

  static public class CustomRecoveryStrategyFactory extends RecoveryStrategyFactory {
    @Override
    public RecoveryStrategy newRecoveryStrategy(CoreContainer cc, CoreDescriptor cd,
        RecoveryStrategy.RecoveryListener recoveryListener) {
      return new CustomRecoveryStrategy(cc, cd, recoveryListener);
    }
  }

}
