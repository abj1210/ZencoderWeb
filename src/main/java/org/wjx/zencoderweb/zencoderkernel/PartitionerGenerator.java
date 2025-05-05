package org.wjx.zencoderweb.zencoderkernel;

import org.wjx.zencoderweb.zencoderkernel.norunner.NextOneGenerator;
import org.wjx.zencoderweb.zencoderkernel.partitioner.Partitioner;
import org.wjx.zencoderweb.zencoderkernel.norunner.wordmap.WordMap;

import java.io.*;
import java.nio.file.Path;


/**
 * A utility class for generating, loading, and saving Partitioner instances.
 * This class provides static methods to create a Partitioner from a dataset,
 * load a serialized Partitioner from a file, and save a Partitioner instance to a file.
 *
 * The `runGenerator` method generates a Partitioner by processing a dataset and applying
 * a Huffman coding strategy based on the provided Huffman number.
 *
 * The `loadPartitioner` method deserializes a Partitioner object from a specified file,
 * allowing for reuse of previously generated Partitioner instances.
 *
 * The `savePartitioner` method serializes a given Partitioner object to a file,
 * enabling persistence of the Partitioner for later use.
 *
 * This class is designed to facilitate the creation and management of Partitioner objects,
 * which are used for encoding and decoding data using a combination of Huffman coding
 * and word mapping techniques.
 */
public class PartitionerGenerator {
    /**
     * Runs the generator to create a Partitioner instance based on the provided dataset,
     * Huffman number, and filename.
     *
     * This method first invokes the `runGenerator` method of `NextOneGenerator` to generate
     * a WordMap from the given dataset. If the generated WordMap is null, the method returns
     * null. Otherwise, it constructs and returns a new Partitioner instance using the WordMap,
     * Huffman number, and filename.
     *
     * @param dataset the dataset used to generate the WordMap; must not be null or empty
     * @param huffmanNumber the Huffman number required for partitioning
     * @param filename the name of the file associated with the Partition*/
    public static Partitioner runGenerator(String dataset, int huffmanNumber, String filename) {
        WordMap map = NextOneGenerator.runGenerator(dataset);
        if(map == null)
            return null;
        return new Partitioner(map, huffmanNumber, filename);
    }

    /**
     * Loads a serialized Partitioner object from the specified file.
     *
     * This method attempts to deserialize a Partitioner instance from the given file.
     * If the file is not found, cannot be read, or does not contain a valid serialized
     * Partitioner object, an error message will be printed to the standard error stream,
     * and null will be returned.
     *
     * @param filename the path to the file containing the serialized Partitioner object
     * @return the deserialized Partitioner object, or null if deserialization fails
     */
    public static Partitioner loadPartitioner(String filename) {
        Partitioner partitioner = null;
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            partitioner = (Partitioner) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return partitioner;
    }

    /**
     * Saves a serialized Partitioner object to a file.
     *
     * This method serializes the provided Partitioner instance and writes it to a file.
     * The file name is determined by invoking the `getFileName` method on the Partitioner object.
     * If an I/O exception occurs during the serialization process, the stack trace of the exception
     * will be printed to the standard error stream.
     *
     * @param partitioner the Partitioner instance to be serialized and saved; must not be null
     */
    public static void savePartitioner(Partitioner partitioner, Path dir) {
        Path fullpath = dir.resolve(partitioner.getFileName());
        try (FileOutputStream fileOut = new FileOutputStream(fullpath.toString());
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(partitioner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
