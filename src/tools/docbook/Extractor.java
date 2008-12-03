/*
 * Copyright (c) 2007-2008 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package docbook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Class to extract code from java source files into XML files that can be
 * included in DocBook books.
 * <p/>
 * The class will search for annotated java source files and extract the annotated source
 * as an XML file.
 */
public class Extractor
  {

  protected String startAnnotation1 = "//@extract-start";
  protected String startAnnotation2 = "<!-- @extract-start";
  protected String endAnnotation = "@extract-end";

  protected File targetDir;
  protected String root;
  private static final String BEGIN_CDATA = "<![CDATA[";
  private static final String END_CDATA = "]]>";

  public Extractor( String root, File targetDir )
    {
    this.root = root;
    this.targetDir = targetDir;
    }

  public static void main( String[] args ) throws Exception
    {

    File sourceDir = new File( args[ 0 ] );
    File targetDir = new File( args[ 1 ] );

    Extractor extractor = new Extractor( sourceDir.getAbsolutePath(), targetDir );
    extractor.extractCode( sourceDir );

    }

  /** @param sourceDir containing java source files */
  public void extractCode( File sourceDir ) throws Exception
    {

    System.out.println( "Extracting dir " + sourceDir );

    // List the source directory. If the file is a dir recurse,
    // if the file is a java file check for Extract annotations
    // otherwise ignore

    File[] elements = sourceDir.listFiles();

    for( int i = 0; i < elements.length; ++i )
      {
      File file = elements[ i ];
      if( file.isDirectory() )
        {
        extractCode( file );
        }
      else if( ( file.getName().endsWith( ".java" ) || file.getName().endsWith( ".xml" ) ) && !file.getName().equals( "Extractor.java" ) )
        {
        extractAnnotatedCode( file );
        } // fi
      } // rof
    }

  public void extractAnnotatedCode( File file ) throws Exception
    {
//    System.out.println( "Handling: " + file );
    String packageName = file.getParentFile().getName();

    BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
    String line = null;
    boolean extract = false;
    BufferedWriter writer = null;

    boolean firstWrite = true;
    int offset = -1;

    while( ( line = reader.readLine() ) != null )
      {

      if( extract )
        {
        if( line.contains( endAnnotation ) )
          {
          closeFile( writer );
          firstWrite = true;
          extract = false;
          writer = null;
          }
        else
          {
          if( line.length() > offset )
            line = line.substring( offset );

          // skip blank lines up to the code
          if( firstWrite && line.trim().length() == 0 )
            continue;

          if( firstWrite )
            firstWrite = false;
          else
            writer.newLine();

          // enable callouts
          line = line.replaceAll( "//(<co.*/>)", END_CDATA +"$1"+ BEGIN_CDATA );

          writer.append( line );
          }
        }
      else
        {
        String start = startAnnotation1;
        offset = line.indexOf( startAnnotation1 );

        if( offset == -1 )
          {
          start = startAnnotation2;
          offset = line.indexOf( startAnnotation2 );
          }

        if( offset > -1 )
          {
          System.out.println( "Extracting from: " + file );
          String name = line.substring( offset + start.length() + 1 ).trim().split( "\\s" )[ 0 ];
          extract = true;
          writer = createXiIncludeFile( packageName, name );
          }
        }
      }

    if( writer != null )
      {
      closeFile( writer );
      }
    }

  public BufferedWriter createXiIncludeFile( String path, String file ) throws Exception
    {

    File targetFile = new File( new File( targetDir, path ), file + ".xml" );

    BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) );

    // write
    writer.write( "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>" );
    writer.newLine();
    writer.write( "<programlisting xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\" xml:lang=\"en\">" + BEGIN_CDATA );
    return writer;
    }

  public void closeFile( BufferedWriter writer ) throws Exception
    {
    writer.write( END_CDATA );
    writer.write( "</programlisting>" );
    writer.flush();
    writer.close();
    }

  }
