import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class FileListMaker {
    private static ArrayList<String> arrayList = new ArrayList<>();
    private static final Scanner in = new Scanner(System.in);
    private static boolean needsToBeSaved = false;
    private static Path currentFile = null;

    public static void main(String[] args) {
        boolean done = false;
        String cmd;

        do {
            displayList();
            displayMenu();
            cmd = SafeInput.getRegExString(in, "Choose an option", "[AaDdIiPpOoSsQq]");

            switch (cmd.toUpperCase()) {
                case "A":
                    addItem();
                    break;
                case "D":
                    deleteItem();
                    break;
                case "I":
                    insertItem();
                    break;
                case "P":
                    printList();
                    break;
                case "O":
                    try {
                        loadListFile();
                    } catch (IOException e) {
                        System.out.println("An error occurred while opening the file.");
                        e.printStackTrace();
                    }
                    break;
                case "S":
                    try {
                        saveListFile();
                    } catch (IOException e) {
                        System.out.println("An error occurred while saving the file.");
                        e.printStackTrace();
                    }
                    break;
                case "Q":
                    done = quitProgram();
                    break;
            }
        } while (!done);

        System.out.println("Goodbye.");
    }

    private static void displayList() {
        System.out.println("\n=== Current List");
        if (arrayList.size() == 0) {
            System.out.println("(The list is empty)");
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                System.out.printf("%3d. %s%n", i + 1, arrayList.get(i));
            }
        }
        System.out.println("===================");
    }

    private static void displayMenu() {
        System.out.println("A - Add an item to the list");
        System.out.println("D - Delete an item from the list");
        System.out.println("I - Insert an item into the list");
        System.out.println("P - Print the list");
        System.out.println("O - Open a list file from disk");
        System.out.println("S - Save the current list file to disk");
        System.out.println("Q - Quit");
    }

    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(in, "Enter the item you want to add");
        arrayList.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem() {
        if (arrayList.size() == 0) {
            System.out.println("The list is empty. There is nothing to delete.");
            return;
        }
        int num = SafeInput.getRangedInt(in, "Enter the number of the item you want to delete", 1, arrayList.size());
        String removed = arrayList.remove(num - 1);
        System.out.println("Deleted: " + removed);
        needsToBeSaved = true;
    }

    private static void insertItem() {
        int num = SafeInput.getRangedInt(in, "Enter the location number to insert at", 1, arrayList.size() + 1);
        String item = SafeInput.getNonZeroLenString(in, "enter the item to insert");
        arrayList.add(num -  1, item);
        needsToBeSaved = true;
    }

    private static void printList() {
        displayList();
    }

    private static void loadListFile() throws IOException {
        if (needsToBeSaved) {
            boolean saveFirst = SafeInput.getYNConfirm(in, "You have unsaved changes. Save the current list first?");
            if (saveFirst) {
                saveListFile();
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setDialogTitle("Open List File");

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("No file selected. Open cancelled.");
            return;
        }

        Path path = chooser.getSelectedFile().toPath();
        List<String> lines = Files.readAllLines(path);

        arrayList.clear();
        arrayList.addAll(lines);
        currentFile = path;
        needsToBeSaved = false;
        System.out.println("loaded " + arrayList.size() + " item from " + path.getFileName());
    }

    private static void saveListFile() throws IOException {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setDialogTitle("Save list file");

            if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                System.out.println("No save location specified. Save cancelled.");
                return;
            }

            File chosen = chooser.getSelectedFile();
            if (!chosen.getName().toLowerCase(Locale.ROOT).endsWith(".txt")) {
                chosen = new File(chosen.getParentFile(), chosen.getName() + ".txt");
            }
            currentFile = chosen.toPath();
        }

        Files.write(currentFile, arrayList);
        needsToBeSaved = false;
        System.out.println("List saved to " + currentFile.getFileName());
    }

    private static boolean quitProgram() {
        if (needsToBeSaved) {
            boolean save = SafeInput.getYNConfirm(in, "You have unsaved changes. Save before quitting?");
            if (save) {
                try {
                    saveListFile();
                } catch (IOException e) {
                    System.out.println("An error occurred while saving the file.");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return SafeInput.getYNConfirm(in, "Are you sure you want to quit?");
    }
}