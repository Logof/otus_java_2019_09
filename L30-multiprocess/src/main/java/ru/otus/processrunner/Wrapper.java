package ru.otus.processrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Wrapper {
  private static final Logger logger = LoggerFactory.getLogger( Wrapper.class );

  public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException {
    //simpleWrap();
    //monitorWrap();
    notifyWrap();
  }

  private static void simpleWrap() throws IOException {
    logger.info( "begin" );

    var currentDir = new File( "./L31-multiprocess/src/main/java" );

    Process process = new ProcessBuilder( "java", "ru.otus.processrunner.Job" )
        .inheritIO()
        .directory( currentDir )
        .start();


    logger.info( "end" );
  }

  private static void monitorWrap() throws IOException, InterruptedException {
    logger.info( "begin" );

    var currentDir = new File( "./L31-multiprocess/src/main/java" );

    var procBuilder = new ProcessBuilder( "java", "ru.otus.processrunner.Job" )
        .inheritIO()
        .directory( currentDir );

    Map<String, String> envProp = procBuilder.environment();
    envProp.put( "propName", "PropOk" );

    var process = procBuilder.start();


    try ( var reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ); ) {
      String line;
      while ( ( line = reader.readLine() ) != null ) {
        logger.info( "proc out: {}", line );
      }
    }

    logger.info( "waiting for process..." );
    process.waitFor( 1, TimeUnit.MINUTES );

    logger.info( "end" );
  }

  private static void notifyWrap() throws IOException {
    logger.info( "begin" );

    var currentDir = new File( "./L31-multiprocess/files" );
    var process = new ProcessBuilder( "cmp", "file1.txt", "file2.txt" )
        .inheritIO()
        .directory( currentDir )
        .start();


    CompletableFuture<Process> compareResult = process.onExit();
    logger.info( "next action 1..." );
    logger.info( "next action 2..." );
    logger.info( "next action 3..." );
    logger.info( "next action 4..." );
    logger.info( "next action 5..." );

    compareResult.thenApply( proc -> {
      if ( proc.exitValue() == 0 ) {
        logger.info( "files equal" );
      } else {
        logger.info( "files NOT equal" );
      }
      return true;
    } );

    logger.info( "end" );
  }
}
