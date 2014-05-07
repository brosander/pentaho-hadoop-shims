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

package org.pentaho.shim.delegate;

import org.pentaho.hadoop.shim.ShimVersion;
import org.pentaho.hadoop.shim.api.Configuration;
import org.pentaho.hbase.shim.spi.HBaseConnection;
import org.pentaho.hbase.shim.spi.HBaseShim;
import org.pentaho.hbase.shim.spi.HBaseShimInterface;
import org.pentaho.shim.auth.HadoopAuthorizationService;
import org.pentaho.shim.auth.HasHadoopAuthorizationService;

public class DelegatingHBaseShim extends HBaseShim implements HasHadoopAuthorizationService, HBaseShimInterface {
  private HBaseShimInterface delegate;

  @Override
  public ShimVersion getVersion() {
    return delegate.getVersion();
  }

  @Override
  public void setHadoopAuthorizationService( HadoopAuthorizationService hadoopAuthorizationService ) {
    delegate = hadoopAuthorizationService.getShim( HBaseShimInterface.class );
  }

  @Override
  public HBaseConnection getHBaseConnection() {
    return delegate.getHBaseConnection();
  }

  @Override
  public void setInfo( Configuration configuration ) {
    delegate.setInfo( configuration );
  }
}
