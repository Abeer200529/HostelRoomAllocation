import java.awt.*;
import java.util.*;
import javax.swing.*;

class Student {
    String name;
    String rollNo;
    String branch;

    Student(String name, String rollNo, String branch) {
        this.name = name;
        this.rollNo = rollNo;
        this.branch = branch;
    }
}

class Room {
    int roomNumber;
    int capacity;
    ArrayList<Student> students;

    Room(int roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.students = new ArrayList<>();
    }

    boolean isFull() {
        return students.size() == capacity;
    }

    boolean isEmpty() {
        return students.size() == 0;
    }

    boolean addStudent(Student s) {
        if (!isFull()) {
            students.add(s);
            return true;
        }
        return false;
    }

    boolean removeStudent(String rollNo) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).rollNo.equalsIgnoreCase(rollNo)) {
                students.remove(i);
                return true;
            }
        }
        return false;
    }
}

class Hostel {
    Room[] rooms;
    Queue<Student> waitingList;

    Hostel(int totalRooms, int capacityPerRoom) {
        rooms = new Room[totalRooms];
        for (int i = 0; i < totalRooms; i++) {
            rooms[i] = new Room(101 + i, capacityPerRoom);
        }
        waitingList = new LinkedList<>();
    }

    void allocateRoom(Student student, JTextArea outputArea) {
        for (Room room : rooms) {
            if (!room.isFull()) {
                room.addStudent(student);
                outputArea.append("Room allocated successfully: " + room.roomNumber + "\n");
                return;
            }
        }
        waitingList.add(student);
        outputArea.append("No room available. Student added to waiting list.\n");
    }

    void removeStudent(String rollNo, JTextArea outputArea) {
        for (Room room : rooms) {
            if (room.removeStudent(rollNo)) {
                outputArea.append("Student removed successfully.\n");

                if (!waitingList.isEmpty()) {
                    Student nextStudent = waitingList.poll();
                    room.addStudent(nextStudent);
                    outputArea.append("Waiting student " + nextStudent.name +
                            " allocated to room " + room.roomNumber + "\n");
                }
                return;
            }
        }
        outputArea.append("Student not found.\n");
    }

    void searchStudent(String name, JTextArea outputArea) {
        for (Room room : rooms) {
            for (Student s : room.students) {
                if (s.name.equalsIgnoreCase(name)) {
                    outputArea.append("Student found in room: " + room.roomNumber + "\n");
                    outputArea.append("Name: " + s.name + ", Roll No: " + s.rollNo + ", Branch: " + s.branch + "\n");
                    return;
                }
            }
        }
        outputArea.append("Student not found.\n");
    }

    void searchByRoom(int roomNumber, JTextArea outputArea) {
        for (Room room : rooms) {
            if (room.roomNumber == roomNumber) {
                outputArea.append("Room " + room.roomNumber + ":\n");
                if (room.students.isEmpty()) {
                    outputArea.append("No students allocated.\n");
                } else {
                    for (Student s : room.students) {
                        outputArea.append(s.name + " | " + s.rollNo + " | " + s.branch + "\n");
                    }
                }
                return;
            }
        }
        outputArea.append("Room not found.\n");
    }

    void displayAllocatedRooms(JTextArea outputArea) {
        for (Room room : rooms) {
            if (!room.isEmpty()) {
                outputArea.append("Room " + room.roomNumber + ":\n");
                for (Student s : room.students) {
                    outputArea.append("  " + s.name + " | " + s.rollNo + " | " + s.branch + "\n");
                }
            }
        }
    }

    void displayEmptyRooms(JTextArea outputArea) {
        boolean found = false;
        for (Room room : rooms) {
            if (room.isEmpty()) {
                outputArea.append("Empty Room: " + room.roomNumber + "\n");
                found = true;
            }
        }
        if (!found) {
            outputArea.append("No empty rooms available.\n");
        }
    }

    void showWaitingList(JTextArea outputArea) {
        if (waitingList.isEmpty()) {
            outputArea.append("Waiting list is empty.\n");
            return;
        }

        outputArea.append("Waiting List:\n");
        for (Student s : waitingList) {
            outputArea.append(s.name + " | " + s.rollNo + " | " + s.branch + "\n");
        }
    }
}

public class HostelAllocation extends JFrame {
    Hostel hostel;
    JTextArea outputArea;

    public HostelAllocation() {
        hostel = new Hostel(20, 2);

        setTitle("Hostel Room Allocation System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JButton allocateBtn = new JButton("Allocate Room");
        JButton removeBtn = new JButton("Remove Student");
        JButton searchNameBtn = new JButton("Search Student by Name");
        JButton searchRoomBtn = new JButton("Search by Room Number");
        JButton displayAllocatedBtn = new JButton("Display Allocated Rooms");
        JButton displayEmptyBtn = new JButton("Display Empty Rooms");
        JButton waitingListBtn = new JButton("Show Waiting List");
        JButton exitBtn = new JButton("Exit");

        panel.add(allocateBtn);
        panel.add(removeBtn);
        panel.add(searchNameBtn);
        panel.add(searchRoomBtn);
        panel.add(displayAllocatedBtn);
        panel.add(displayEmptyBtn);
        panel.add(waitingListBtn);
        panel.add(exitBtn);

        add(panel, BorderLayout.SOUTH);

        allocateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter name:");
            String roll = JOptionPane.showInputDialog(this, "Enter roll no:");
            String branch = JOptionPane.showInputDialog(this, "Enter branch:");
            if (name != null && roll != null && branch != null) {
                hostel.allocateRoom(new Student(name, roll, branch), outputArea);
            }
        });

        removeBtn.addActionListener(e -> {
            String roll = JOptionPane.showInputDialog(this, "Enter roll no to remove:");
            if (roll != null) hostel.removeStudent(roll, outputArea);
        });

        searchNameBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter student name:");
            if (name != null) hostel.searchStudent(name, outputArea);
        });

        searchRoomBtn.addActionListener(e -> {
            String roomStr = JOptionPane.showInputDialog(this, "Enter room number:");
            if (roomStr != null) {
                try {
                    int roomNo = Integer.parseInt(roomStr);
                    hostel.searchByRoom(roomNo, outputArea);
                } catch (NumberFormatException ex) {
                    outputArea.append("Invalid room number.\n");
                }
            }
        });

        displayAllocatedBtn.addActionListener(e -> hostel.displayAllocatedRooms(outputArea));
        displayEmptyBtn.addActionListener(e -> hostel.displayEmptyRooms(outputArea));
        waitingListBtn.addActionListener(e -> hostel.showWaitingList(outputArea));
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    public static void main(String[] args) {
        new HostelAllocation();
    }
}
