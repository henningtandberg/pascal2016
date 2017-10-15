README Pascal2016
henninpt
20.09.16

Compiling:
    run command "ant" in folder "pascal2016"

Clean up:
    run command "ant clean" in folder "pascal2016"


Del 1:

    Testing Scanner:
        java -jar pascal2016.jar -logS -testscanner <test_file.pas>

    Comments:
        I metoden createCharToken() vil det oppstå en scanner error hvis det er
        mer enn én character mellom ''. Dette kommenterer jeg siden det i en av
        testfilene (ulovlige_tegn.pas) var det skrevet '''' (to characters i mellom '') inne i en write metode.
        Dette tolket jeg som en skrivefeil siden det alltid kun bør kunne være én character mellom ''.
        Så dette vil være lovlig ''' men ikke dette ''''. Det er heller ikke lovlig i min scanner å ha en tom
        character. Altså bare ''.

Del 2:

    Testing parser:
        java -jar pascal2016.jar -logP -testparser <test_file.pas>

    Comments:
        Empty Statement:
            Jeg er usikker på om jeg har tolket statmentlisten helt rikitg
            da jeg ofte får en ekstra EmptyStatm på slutten av en statmelist.
            Slik det fungerer nå, vil semicolonToken bli skipet av EmptyStatm
            ved de fleste tilfeller.
            Dette har jeg valgt å la gå siden det ikke har skapt noen problemer
            så langt.

        Klasser:
            Jeg tolket det først slik at Type og dens underklasser skulle være de
            Types som allerede var laget i referansekompilatoren. Men da jeg jobbet
            med de ferdiglagde klassene støtte jeg på noen komplikasjoner. Derfor endte
            jeg opp med å opprette nye klasser som hører til parser packagen som alle
            er underklasser av PascalSyntax. Dersom det er ment at de Type-klassene som
            allerede var laget skulle brukes i parser, ser jeg ikke for meg at det er en
            alt for stor jobb å merge de jeg har laget med de som var der fra før.

        Metoder:
            Det er var noen abstrakte metoder i PascalDecl som jeg tolket som om ikke
            skulle brukes i del 2.
            Disse er:
                abstract void checkWhetherAssignable(PascalSyntax where);
                abstract void checkWhetherFunction(PascalSyntax where);
                abstract void checkWhetherProcedure(PascalSyntax where);
                abstract void checkWhetherValue(PascalSyntax where);

            Siden de ikke kan huske at de ble snakket om i forelesningen og det ikke sto
            noe om dem (som jeg fikk med meg) i kapitelet i kompendiet om parseren
            unlot jeg å implenetere dem, da jeg ikke så noen foreløpig bruk for dem i parseren.

        Pretty Print:
            prettyPrint-metodene skal fungere som de skal, men jeg fikk bare testet det på gcd.pas,
            easter.pas og en egenskrevet kode. Derfor kan det hende at jeg har oversett noe..

        Kommentarer i koden
        og filer:
            Siden jeg sjeldent legger inn utdypende kommentarer når jeg skriver kode,
            er kommentarene nå litt snaue. Siden det ble litt mye å gå over all koden
            og skrive en utdypende kommentar for hver metode til slutt. Har jeg valgt å skrive
            litt utdypende for eknkelte metoder og latt de fleste være ukommenteret.

            Når det gjelder klassefilene har jeg ikke helt vist hvordan jeg skulle best sortere dem.
            Jeg har endt opp med å la alle klasser og sub-klasser ligge i parser-mappen. I mangel
            på bedre alternativer. Dette gjør det ganske uoversiktelig.

Del 2 versjon 2:

    Det som måtte fikses:
        -   Måten semicolonToken blir tolket mellom statments. Gå bort fra å tolke emtpy statment
            som et semicolon.
        -   Mangel på linjeskift enkelte steder i prettyPrint.
        -   Fikse ProcCallStatm og FuncCall slik at føste arument ikke printes to ganger.
        -   Generelt sett jobbe med å gjøre prettyPrint finere.

    Hvordan jeg har fikset det:
        -   Jeg har løst problemet med EmptyStatm ved å først og fremst ikke la EmptyStatm skipe
            en semicolonToken. For at koden da skulle kjøre ble jeg nødt til å endre på while-loopen
            i StatmList slik som ble foreslått i tilbakemeldingen.
            Nåværende while-loop:
                while ( s.curToken.kind != endToken ) {
                    if( s.curToken.kind == semicolonToken)
                        s.skip(semicolonToken);

                    stl.stList.add( Statement.parse(s) );
                }

            Tidligere while-loop:
                while ( s.curToken.kind != endToken )
                    stl.stList.add( Statement.parse(s) );

            Foreslått while-loop:
                while ( s.curToken.kind == semicolonToken ) {
                    s.readNextToken();
                    stl.stList.add( Statement.parse(s) );
                }

            Grunnen til at jeg ikke har brukt den foreslåtte er for å spare tid. Det er nok en
            litt penere måte å gjøre det på, men siden jeg måtte gått gjennom enda flere klasser
            for å få det til å fungere med den foreslåtte har jeg bare lagt til en sjekk for
            semicolonToken i den loopen jeg hadde fra før. Dette fungerer helt fint, og parseren
            gir riktig output og bygger treet riktig. Som er det viktigste.

        -   Problemet med et at første parameter i proccall og funccall ble skrevet ut to ganger
            kom av en liten skrivefeil der en loop staret på 0 istedet for 1.

        -   Nå skal indents/ linjskift osv være fikset i prettyPrint. Og outputen skal være
            generelt sett mye penere.

Del 3:

    Testing Scanner:
        Kun Binding:
            java -jar pascal2016.jar -logB <test_file.pas>
        Kun Typechecks:
            java -jar pascal2016.jar -logT <test_file.pas>
        Begge deler:
            java -jar pascal2016.jar -testchecker <test_file.pas>

    Comments:
        Jeg har kjørt alle testprogrammene i som ligger under oblig/feil/del3
        og alle som ligger under oblig/test på fellesområdet. Derfor er jeg ganske
        sikker på at alt skal være på plass denne gangen. Jeg har også gått gjennom
        koden og kommentert litt bedre denne gangen.

        Det er kun to ting å merke seg (Som jeg har oppdaget):
            1. Jeg har sett at i uskrift forslaget er det testet på
                høyre og venstre side av operatorer. Denne denne testen
                har jeg slått sammen.
            2. Måten å bruke arrays på ble jeg litt usikker på, så det kan
                hende at jeg bommet på parsing, og check på den. JEg har sett
                på noen andre tutorials om pasacal som forvirret meg litt.

        Annet:
            - Rettet noen det med lower case i scanner, som ble påpekt ved
            første innlevering.
            - Rettet på prettyPrint til parser.ArrayType

Del 4:

    Rettet utifra tilbakemeldinger;

        Parser:

        - Funksjons- og prosedyrekall med tom argumentliste skal ikke ha
          parenteser etter nanvnet.

        - Parseren avsluttet aldri når den ble kjørt på extra-right-par.pas.

        Checker:

        - Library deklarerer ikke med verdier

        * Jeg har ikke rettet på checkeren der den tester høyre og venstre side
          av en operator sammen. Her har jeg ikke helt skjønt hva jeg skulle gjøre.

    Jeg har testet med alle filer i inf2100.at.ifi.uio.no/test og det ser ut til å gi
    riktig resulat og jeg er derfor ganske sikker på at alt skal fungere.

    To ting å påpeke:

    - Jeg har gjort regning med at det skal testes på ifimaskiner som har tilgang til
    filene som trengs for å bruke write_char, int og bool.

    - Siden FuncDecl, ProcDecl og Program har behov for å genere litt kode mellom Block
    sin kodegenerering har jeg valgt å legge til en metode i block som kun generer kode
    for Func/ProcDecls.
