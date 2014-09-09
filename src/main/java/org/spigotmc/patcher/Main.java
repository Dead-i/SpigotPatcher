package org.spigotmc.patcher;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.util.HashMap;

import com.google.gson.Gson;
import net.md_5.jbeat.Patcher;

public class Main
{
    private static HashMap<String, Object> result = new HashMap<String, Object>();

    public static void main(String[] args) throws Exception
    {
        patch( args );
        if ( !result.isEmpty() )
        {
            System.out.println( new Gson().toJson( result ) );
        }
    }

    public static void patch(String[] args) throws Exception
    {
        if ( args.length != 3 )
        {
            result.put( "error", 1 );
            return;
        }

        File originalFile = new File( args[0] );
        File patchFile = new File( args[1] );
        File outputFile = new File( args[2] );

        if ( !originalFile.canRead() )
        {
            result.put( "error", 2 );
            return;
        }
        if ( !patchFile.canRead() )
        {
            result.put( "error", 3 );
            return;
        }
        if ( outputFile.exists() )
        {
            result.put( "error", 4 );
            return;
        }
        if ( !outputFile.createNewFile() )
        {
            result.put( "error", 5 );
            return;
        }

        result.put( "inputsum", Files.hash( originalFile, Hashing.md5() ).toString() );
        result.put( "patchsum", Files.hash( patchFile, Hashing.md5() ).toString() );

        try
        {
            new Patcher( patchFile, originalFile, outputFile ).patch();
        } catch ( Exception ex )
        {
            System.err.println( "***** Exception occured whilst patching file!" );
            ex.printStackTrace();
            outputFile.delete();
            return;
        }

        result.put( "outputsum", Files.hash( outputFile, Hashing.md5() ).toString() );
        result.put( "success", 1 );
    }
}
