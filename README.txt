README Pascal2016
henninpt
20.09.16

Compiling:
    run command "ant" in folder "pascal2016"

Clean up:
    run command "ant clean" in folder "pascal2016"

Testing Scanner:
    java -jar pascal2016.jar -logS -testscanner <test_file.pas>

Comments:
    I metoden createCharToken() vil det oppstå en scanner error hvis det er
    mer enn én character mellom ''. Dette kommenterer jeg siden det i en av
    testfilene (ulovlige_tegn.pas) var det skrevet '''' (to characters i mellom '') inne i en write metode.
    Dette tolket jeg som en skrivefeil siden det alltid kun bør kunne være én character mellom ''.
    Så dette vil være lovlig ''' men ikke dette ''''. Det er heller ikke lovlig i min scanner å ha en tom
    character. Altså bare ''.  
