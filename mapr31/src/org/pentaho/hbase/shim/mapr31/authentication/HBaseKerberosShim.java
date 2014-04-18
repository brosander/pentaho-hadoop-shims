/*******************************************************************************
*
* Pentaho Big Data
*
* Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

package org.pentaho.hbase.shim.mapr31.authentication;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.security.auth.login.LoginContext;

import org.apache.hadoop.security.HadoopKerberosName;
import org.pentaho.hadoop.shim.api.Configuration;
import org.pentaho.hadoop.shim.common.ConfigurationProxy;
import org.pentaho.hadoop.shim.common.ShimUtils;
import org.pentaho.hadoop.shim.mapr31.authorization.KerberosInvocationHandler;
import org.pentaho.hbase.shim.mapr31.MapRHBaseConnection;
import org.pentaho.hbase.shim.mapr31.MapRHBaseShim;
import org.pentaho.hbase.shim.spi.HBaseBytesUtilShim;
import org.pentaho.hbase.shim.spi.HBaseConnection;
import org.pentaho.hbase.shim.spi.HBaseConnectionInterface;
import org.pentaho.hbase.shim.spi.HBaseShimInterface;
import org.pentaho.shim.delegate.DelegatingHBaseConnection;

public class HBaseKerberosShim extends MapRHBaseShim implements HBaseShimInterface {
  public static final String PENTAHO_LOGIN_CONTEXT_UUID = "pentaho.login.context.uuid";
  private final LoginContext loginContext;
  private final String loginContextUuid;

  public HBaseKerberosShim( LoginContext loginContext ) {
    this.loginContext = loginContext;
    loginContextUuid = UUID.randomUUID().toString();
    HBaseKerberosUserProvider.setLoginContext( loginContextUuid, loginContext );
  }

  @Override
  public HBaseConnection getHBaseConnection() {
    return new DelegatingHBaseConnection( KerberosInvocationHandler.forObject( loginContext, new MapRHBaseConnection() {

      @Override
      public void configureConnection( Properties connProps, List<String> logMessages ) throws Exception {
        super.configureConnection( connProps, logMessages );
        setInfo( new ConfigurationProxy( m_config ) );
      }
    }, new HashSet<Class<?>>( Arrays.<Class<?>> asList( HBaseShimInterface.class, HBaseConnectionInterface.class,
        HBaseBytesUtilShim.class ) ) ) );
  }
  
  @Override
  public void setInfo( Configuration configuration ) {
    super.setInfo( configuration );
    try {
      HadoopKerberosName.setConfiguration( ShimUtils.asConfiguration( configuration ) );
    } catch ( IOException e ) {
      throw new RuntimeException( e );
    }
    configuration.set( "hbase.client.userprovider.class", HBaseKerberosUserProvider.class.getCanonicalName() );
    configuration.set( PENTAHO_LOGIN_CONTEXT_UUID, loginContextUuid );
  }
}
