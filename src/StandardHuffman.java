import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Name: Omar Khaled Mohy El-din
 * ID: 20190353
 *
 * Name: Omar Atef Mohamed Yassin
 * ID: 20190356
 *
 * Group: S2
 * Department: CS
 * */

//Class made to work with collection sorts where it sorts based on probability.
class CustomComparatorValue implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return o2.getProbability().compareTo(o1.getProbability());
    }
}

class Node{
    public String value;
    public Double probability;
    public String BinaryCode;
    public Node leftChild;
    public Node rightChild;

    public Node(){
        value="";
        probability=0.0;
        BinaryCode = "";
        leftChild = rightChild = null;
    }

    public Node(String value,Double probability){
        this.value = value;
        this.probability = probability;
        this.BinaryCode="";
        leftChild = rightChild = null;
    }

    public String getValue() {
        return value;
    }

    public Double getProbability() {
        return probability;
    }

    @Override
    public boolean equals(Object object){
        if (object instanceof Node)
        {
            return (this.value.equals(((Node) object).value));
        }
        return false;
    }

    @Override
    public String toString() {
        return this.value+" -> "+this.probability;
    }
}

public class StandardHuffman {
    private Hashtable<String,String> binaryCodeTable;
    private ArrayList<Node> codeProb_pair;
    private Node root;

    public StandardHuffman(){
        binaryCodeTable = new Hashtable<>();
        codeProb_pair = new ArrayList<>();
        root = null;
    }

    public int getSize(){
        return codeProb_pair.size();
    }

    public void printTable(){
        for(int i=0; i<codeProb_pair.size(); i++){
            System.out.println(codeProb_pair.get(i));
        }
    }

    public void computeProbability(String data){
        char target ='\0';
        for(int i=0; i<data.length(); i++){
            target = data.charAt(i);
            int counter = 0;
            for(int j=0; j<data.length(); j++){
                if(data.charAt(j)==target){
                    counter++;
                }
            }

            Node temp = new Node(Character.toString(target),counter/(double)data.length());
            if(!(codeProb_pair.contains(temp))) {
                System.out.println("Count of "+target+": "+counter);
                codeProb_pair.add(temp);
            }
        }
        System.out.println("***************************************************");
    }

    public void levelOrderBinaryCodeAssign(Node node){
        Queue<Node>q = new LinkedList<>();
        if(node==null){
            System.out.println("Tree is empty!");
            return;
        }
        q.add(node);
        while (!q.isEmpty()){
            Node cur = q.peek();
            System.out.println(cur.value +" "+cur.BinaryCode);
            String parentCode = q.peek().BinaryCode;
            if(cur.leftChild!=null){
                cur.leftChild.BinaryCode = parentCode;
                cur.leftChild.BinaryCode += "0";
                q.add(cur.leftChild);
                if(cur.leftChild.getValue().length()==1){
                    binaryCodeTable.put(cur.leftChild.value,cur.leftChild.BinaryCode);
                }
            }
            if(cur.rightChild!=null){
                cur.rightChild.BinaryCode = parentCode;
                cur.rightChild.BinaryCode += "1";
                q.add(cur.rightChild);
                if(cur.rightChild.getValue().length()==1){
                    binaryCodeTable.put(cur.rightChild.value,cur.rightChild.BinaryCode);
                }
            }
            q.remove();
        }
    }

    public void compress(String data){
        //Compute probability of each character in the input.
        computeProbability(data);

        //Sorts the array of characters.
        Collections.sort(codeProb_pair,new CustomComparatorValue());

        //Prints arraylist of characters and combined strings.
        printTable();
        System.out.println("***************************************************");

        //Builds the tree.
        while (codeProb_pair.size() != 1){
            String lastStr = codeProb_pair.get(getSize()-2).getValue() + codeProb_pair.get(getSize()-1).getValue();
            Double lastPrb = codeProb_pair.get(getSize()-1).getProbability() + codeProb_pair.get(getSize()-2).getProbability();

            Node newNode = new Node(lastStr,lastPrb);
            newNode.leftChild=codeProb_pair.get(getSize()-2);
            newNode.rightChild = codeProb_pair.get(getSize()-1);

            codeProb_pair.remove(getSize()-1);
            codeProb_pair.remove(getSize()-1);
            codeProb_pair.add(newNode);

            Collections.sort(codeProb_pair,new CustomComparatorValue());
        }

        //Assigns root to the main node of the tree so as to traverse it after.
        this.root = codeProb_pair.get(0);

        //Builds Hashtable through binary tree node codes.
        levelOrderBinaryCodeAssign(root);
        System.out.println("***************************************************\n");
        System.out.println("Hashtable entries: ");
        //Print our dictionary
        System.out.println(this.binaryCodeTable+"\n");
        System.out.println("***************************************************");

        //Write our dictionary to a file
        writeDictionaryFile();

        //Writes the compressed data into a file
        compressedDataToFile(data);
    }

    public void compressedDataToFile(String data){
        try {
            FileWriter fileWriter = new FileWriter("Compressed.txt",false);
            for (int i = 0 ; i<data.length();i++){
                if (binaryCodeTable.containsKey(Character.toString(data.charAt(i))))
                    fileWriter.write(binaryCodeTable.get(Character.toString(data.charAt(i))));
            }
            fileWriter.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void writeDictionaryFile(){
        try{
            FileWriter fileWriter = new FileWriter("Dictionary.txt",false);
            Set<Map.Entry<String,String>> entries = binaryCodeTable.entrySet();
            for(Map.Entry<String,String> entry : entries){
                fileWriter.write(entry.getKey()+":"+entry.getValue()+"\n");
            }
            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StandardHuffman standardHuffman = new StandardHuffman();

        String data = "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEETTTTTTTTTTT" +
                "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" +
                "OOOOOIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" +
                "NSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRHHHHH" +
                "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" +
                "DDDDDDDDDDCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCUUUUUUUUUUUUUUUUUUUUUUUUUUU";


        standardHuffman.compress(data);

        standardHuffman.decompress();
    }

    public void decompress()  {
        System.out.println("\nDecompressing: \n");
        try{
            //Reads the dictionary file.
            BufferedReader dictionaryFileReader = new BufferedReader( new FileReader("Dictionary.txt"));

            //Stores dictionary file as lines in a string array.
            ArrayList<String> fileEntries = new ArrayList<>();

            //Reads dictionary file buffered reader line by line and stores lines into dictionaryData Arraylist.
            String values = dictionaryFileReader.readLine();

            //Reads each line from Dictionary.txt and writes it to an array index at fileEntries ArrayList.
            while (values!=null){
                fileEntries.add(values);
                values=dictionaryFileReader.readLine();
            }
            dictionaryFileReader.close();

            //Reads only binary values from each entry in file.
            ArrayList <String> dictionaryBinaryEntries =new ArrayList<>();

            //Stores binary values to dictionaryBinaryEntries
            for (int i = 0 ;i<fileEntries.size();i++){
                dictionaryBinaryEntries.add(fileEntries.get(i).substring(2));
            }

            //Reads compressed data bit sequences from Compressed.txt file.
            BufferedReader compressedDataFile = new BufferedReader( new FileReader("Compressed.txt"));

            //Take a line from Compressed.txt file.
            String compressedData = compressedDataFile.readLine();

            compressedDataFile.close();

            /*
             * The following loop does the decompression operation using dictionaryBinaryEntries and fileEntries that contains key:value pairs.
             */
            String temp = "";
            String decompressed= "";
            for (int i = 0 ;i<compressedData.length();i++){
                temp+=compressedData.charAt(i);
                if (dictionaryBinaryEntries.contains(temp)){
                    int index = dictionaryBinaryEntries.indexOf(temp);
                    decompressed += fileEntries.get(index).substring(0,1);
                    temp="";
                }
            }

            System.out.println(decompressed);
            System.out.println("\n***************************************************");
            System.out.println("Original Size = "+decompressed.length()*8 +" Bits");
            System.out.println("Compressed Size = "+compressedData.length()+" Bits");
            double ratio = (double) compressedData.length() / ((int)(decompressed.length()*8));
            System.out.println("Compression Ratio = "+ratio);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

