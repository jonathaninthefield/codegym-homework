package com.codegym.task.task17.task1711;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 
CRUD 2
Batch CrUD: multiple Creations, Updates, Deletions.

The program runs with one of the following sets of arguments:
-c name1 sex1 bd1 name2 sex2 bd2 ...
-u id1 name1 sex1 bd1 id2 name2 sex2 bd2 ...
-d id1 id2 id3 id4 ...
-i id1 id2 id3 id4 ...

Argument values:
name (String)
sex ("m" or "f")
bd (birth date in the following format: 04 15 1990)
-c (adds all people with the specified arguments to the end of allPeople; displays their ids in order)
-u (updates the corresponding data of people with the specified ids)
-d (performs the logical deletion of the person with the specified id; replaces all of its data with null)
-i (displays information about all people with the specified ids: name sex bd)

The id corresponds to the index in the list.
The birth date format is Apr 15 1990.
All the people should be stored in allPeople.
The order in which data is displayed corresponds to the order in which is input.
Be sure the program is thread safe (works correctly with multiple threads without corrupting the data).
Use Locale.ENGLISH as the second argument for SimpleDateFormat.

Example output for the -i argument with two ids:
Washington m Apr 15 1990
Ross f Apr 25 1997

Requirements:
1. The Solution class must contain a public volatile List<Person> field called allPeople.
2. The Solution class must have a static block where two people are added to the allPeople list.
3. With the -c argument, the program must add all people with the specified arguments to the
end of the allPeople list, and display the id of each of them.
4. With the -u argument, the program must update the data of the people with the specified ids
in the allPeople list.
5. With the -d argument, the program must perform the logical deletion of the people with the
specified ids in the allPeople list.
6. With the -i argument, the program should display data about all the people with the specified ids
according to the format specified in the task.
7. The Solution class's main method must contain a switch statement based on args[0].
8. Each case label in the switch statement must have a synchronization block for allPeople.

*/

public class Solution {
    public static final List<Person> allPeople = new ArrayList<>();

    static {
        allPeople.add(Person.createMale("Donald Chump", new Date()));  // id=0
        allPeople.add(Person.createMale("Larry Gates", new Date()));  // id=1
    }

    public static void main(String[] args) {
        try {
        switch(args[0]) {
            case "-c":
                synchronized (allPeople) {
                    createPeople(args);
                }
                break;
            case "-u":
                synchronized (allPeople) {
                    updatePeople(args);
                }
                break;
            case "-d":
                synchronized (allPeople) {
                    deletePeople(args);
                }
                break;
            case "-i":
                synchronized (allPeople) {
                    indexPeople(args);
                }
                break;
            default:
                System.out.println("Invalid argument");
        } } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }

    public static void createPeople(String[] args) throws ParseException {
        String name = null;
        String sex = null;
        Date birthDate = null;

        for (String string: args) {
            if(!string.contentEquals("-c")) {
                    if (isBirthDate(string))
                        birthDate = parseBirthDate(string);
                    else if (isSex(string))
                        sex = string;
                    else
                        name = string;
                if(name != null && sex != null && birthDate!= null) {
                    if (sex.contentEquals("m"))
                        allPeople.add(Person.createMale(name, birthDate));
                    else
                        allPeople.add(Person.createFemale(name, birthDate));
                    System.out.println(allPeople.size() - 1);
                    name = null;
                    sex = null;
                    birthDate = null;
                }
            }
        }
    }

    public static void updatePeople(String[] args) throws ParseException {

        String name = null;
        Sex sex = null;
        Date birthDate = null;
        Integer id = null;

        for (String string: args) {
            if(!string.contentEquals("-u")) {
                    if (isBirthDate(string))
                        birthDate = parseBirthDate(string);
                    else if (isSex(string)) {
                        if (string.contentEquals("m"))
                            sex = Sex.MALE;
                        else
                            sex = Sex.FEMALE;
                    } else if (isID(string))
                        id = Integer.parseInt(string);
                    else
                        name = string;

                if(id != null && name != null && sex != null && birthDate != null) {
                    final Person PERSON = allPeople.get(id);
                    PERSON.setName(name);
                    PERSON.setSex(sex);
                    PERSON.setBirthDate(birthDate);
                    name = null;
                    sex = null;
                    birthDate = null;
                    id = null;
                }
            }
        }
    }

    public static void deletePeople(String[] args) {
        for (String string:args) {
            if(!string.contentEquals("-d")) {
                int id = Integer.parseInt(string);
                allPeople.get(id).setName(null);
                allPeople.get(id).setSex(null);
                allPeople.get(id).setBirthDate(null);
            }
        }
    }

    public static void indexPeople(String[] args) {
        for (String string:args) {
            if(!string.contentEquals("-i")) {
                int id = Integer.parseInt(string);
                String name = allPeople.get(id).getName();
                Sex enumSex = allPeople.get(id).getSex();
                String sex;
                {
                    if (enumSex.equals(Sex.MALE))
                        sex = "m";
                    else
                        sex = "f";
                }
                String birthDate = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
                        .format(allPeople.get(id).getBirthDate());
                System.out.println(String.format("%1s%2s %3s", name, sex, birthDate));
            }
        }
    }

    public static Date parseBirthDate(String birthDate) throws ParseException {
        return new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH).parse(birthDate);
    }

    public static boolean isBirthDate(String string) {
        Pattern pattern = Pattern.compile("\\d+\\s\\d+\\s\\d+");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public static boolean isSex(String string) {
        return string.contentEquals("m")
                || string.contentEquals("f");
    }

    public static boolean isID(String string) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
}
