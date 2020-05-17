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
        /*
        Object Oriented alternative
        You are procedurally looping over a string to execute a command as it's parsing a string.
        
        Identify the two main responsibilities/tasks of this program:
        1. Parsing the user input string into a command and person(s)
        2. Executing the desired command with the supplied person(s)
        
        This means you should have a minimum of two classes: CommandParser and PersonRepository.
        
        CommandParser would have some method parseCommand(String). That should return some object that can be passed
        through the rest of the system. You never want to pass raw user input through your program. You want to have
        some type of Controller which transforms the user input into input objects -- or rather, you want a Controller
        to use a Parser to create those objects, which the Controller then passes to other objects (like the PersonRepo).
        
        So, you've identified an extra object: the input object that the Parser outputs and the Repo accepts as input. ...
        */
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
            /*
            This is common, loop over something and only do something if some condition (!= -c) is true. This can lead to 
            excessive indenting. It's preferable to think of it as "if some condition is false, then skip."
            if (string.contentEquals("-c")) {
                continue;
            }
            // Continue processing
            
            See how now the rest of the code doesn't have to bbe indented?
            */
            if(!string.contentEquals("-c")) {
                    // Code style: you've indented twice instead of once here. Also, unless you're following some coding
                    // style that says to, you should never omit curly bbraces on if/else statements, even though they are
                    // optional
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
                    System.out.println(allPeople.size() - 1);   //output index number of people added
                    name = null;
                    sex = null;
                    birthDate = null;
                }
            }
        }
    }

    public static void updatePeople(String[] args) throws ParseException {

        /**
        This whole section was copy/pasted from abbove and continued below. Remember: Copy and Paste is Copy and Waste.
        
        You want to keep your code DRY: Don't Repeat Yourself. When you find yourself typing something out again or 
        going for that COPY, STOP. Think, how can I make this into reusable code? This means REFACTORING: rewriting portions
        of code without changing functionality, for the purpose of making it extendible, maintainable, or clarity.
        
        I might create some new PRIVATE method called parseNextPerson(string): <name:String, sex:Sex,birthDate:Date, remainingString:String>
        
        That way you loop over that, while that method gives you another person, then do something for that information.
        */
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
        Pattern pattern = Pattern.compile("\\d\\d\\s\\d\\d\\s\\d\\d\\d\\d");
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
