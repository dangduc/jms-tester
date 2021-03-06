/*
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
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
package com.fusesource.forge.jmstest.config.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;

public class DefaultJMSConnectionProvider implements JMSConnectionProvider {

  private String user = null;
  private String password = null;

  private ConnectionFactory cf = null;

  public void setConnectionFactory(ConnectionFactory cf) {
    this.cf = cf;
  }

  public ConnectionFactory getConnectionFactory() throws Exception {
    return cf;
  }

  public Connection getConnection() throws Exception {
    final String user = getUser();
    final String password = getPassword();

    Connection conn = null;
    if (user != null & password != null && password.length() > 0) {
      conn = getConnectionFactory().createConnection(user, password);
    } else {
      conn = getConnectionFactory().createConnection();
    }
    return conn;
  }

  public final QueueConnection getQueueConnection() throws Exception {
    return (QueueConnection) getConnection();
  }

  public final TopicConnection getTopicConnection() throws Exception {
    return (TopicConnection) getConnection();
  }

  public final String getUser() {
    return user;
  }

  public final void setUser(final String user) {
    this.user = user;
  }

  public final String getPassword() {
    return password;
  }

  public final void setPassword(final String password) {
    this.password = password;
  }
}
