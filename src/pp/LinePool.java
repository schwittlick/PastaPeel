package pp;

import processing.core.PApplet;
import processing.core.PVector;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Marcel on 12.01.14.
 */
public class LinePool {
    private PApplet parent;

    // holds the mouse position of the last click
    private PVector lastClick;
    private boolean firstClick;
    // the container holding all previously drawn lines
    private ArrayList< Line > lineContainer;
    // container holding the specific color for each line. TODO: java.util.HashMap should do it, too.
    private ArrayList< Integer > colorContainer;
    private ColorChooser colorChooser;

    /*
    LinePool constructor.
     */
    public LinePool( PApplet parent ) {
        this.parent = parent;

        this.colorChooser = new ColorChooser( this.parent );

        this.lineContainer = new ArrayList<>();
        this.colorContainer = new ArrayList<>();

        this.firstClick = true;
    }

    /*
    Adds a line to the container.

    @param Line l the line which should be added
    @param int lineColor the color of the added line
     */
    public void addLine( Line l, int lineColor ) {
        lineContainer.add( l );
        // adds a random color for now.
        colorContainer.add( lineColor );
    }

    /*
    draws all previously laved lines.

    @param lineWidth the width of the line
     */
    public void drawLines( int lineWidth ) {
        int index = 0;
        for( Line l : lineContainer ){
            int lineColor = colorContainer.get( index );
            l.draw( lineColor, lineWidth );
            index++;
        }
    }

    /*
    every time the mouse is pressed another start-/endpoint of lines is stored.

    @param Grid grid the Grid, which contains information about its size etc.
    @param int lineColor the currently selected line color
     */
    public void mousePressed( Grid grid, int lineColor ) {
        // calculating the X and Y coordinates of the lines snapped to the grid
        int xIndex = ( int ) ( grid.getActiveIndizesXY().x );
        int yIndex = ( int ) ( grid.getActiveIndizesXY().y) ;
        int cellWidth = ( int ) ( grid.getCellWidth() );
        int cellHeight = ( int ) ( grid.getCellHeight() );
        int snappedX = xIndex * cellWidth + ( cellWidth / 2 );
        int snappedY = yIndex * cellHeight + ( cellHeight / 2 );

        // always ignoring the first click of a line
        if( !firstClick ) {
            this.addLine( new Line( parent, ( int ) ( lastClick.x ), ( int ) ( lastClick.y ), snappedX, snappedY ), lineColor );
        }

        // saving the current snapped X for the next click
        lastClick = new PVector( snappedX, snappedY );

        // loop this
        firstClick = !firstClick;
    }

    /*
    Saves all currently created lines to a file.

    @param String fileName the name of the file
     */
    public void saveToFile( String fileName ) {
        BufferedWriter linePoolWriter = null;
        try {
            File file = new File( fileName );

            linePoolWriter = new BufferedWriter( new FileWriter( file ) );
            int counter = 0;
            // the delimiter, by which all tokens are separated
            String delimiter = ";";
            for( Line l : lineContainer ) {
                String lineToWrite = l.getStart().x + delimiter + l.getStart().y + delimiter +
                        l.getEnd().x + delimiter + l.getEnd().y + delimiter +
                        colorChooser.getIndexByColor( colorContainer.get( counter ) ) + System.getProperty("line.separator");
                linePoolWriter.write( lineToWrite );
                counter++;
            }

        } catch( Exception e ) {
            System.err.println( "Couldn't save file. Abort." );
        } finally {
            try {
              linePoolWriter.close();
            } catch ( NullPointerException e ){
                System.err.println( "Couldn't close the BufferedWriter. Aborting." );
            } catch( IOException e ) {
                System.err.println( "Couldn't close the BufferedWriter. Aborting." );
            }
        }
    }

    /*
    Loads lines from a file.

    @param String fileName the filename where the lines are saved
     */
    public void loadFromFile( String fileName ) {
        BufferedReader fileReader = null;
        String delimiter = ";";
        String currentLine;
        try {
            fileReader = new BufferedReader( new FileReader( fileName ) );
            while( ( currentLine = fileReader.readLine() ) != null ) {
                String[] fields = currentLine.split( delimiter );
                PVector start = new PVector( Float.parseFloat( fields[ 0 ] ), Float.parseFloat( fields[ 1 ] ) );
                PVector end = new PVector( Float.parseFloat( fields[ 2 ] ), Float.parseFloat( fields[ 3 ] ) );
                int colorIndex = Integer.parseInt( fields[ 4 ] );
                Line loadedLine = new Line( parent, start.x, start.y, end.x, end.y );
                lineContainer.add( loadedLine );
                colorContainer.add( colorChooser.getColor( colorIndex ) );
            }
        } catch( Exception e ) {
            System.err.println( "Couldn't load file. Aborting." );
        } finally {
            if( fileReader != null ){
                try {
                    fileReader.close();
                } catch ( Exception e ) {
                    System.err.println( "Couldn't close BufferedReader. Aborting." );
                }
            }
        }
    }
 }
