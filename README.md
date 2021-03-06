#Dokumentation zum Testdriven Development mit Android

[Hochschule der Medien Stuttgart](http://www.hdm-stuttgart.de/)  
[Studiengang Computer Science and Media](http://www.mi.hdm-stuttgart.de/csm)  
WS 2013/2014

Autoren:
* Friedolin Förder ff026@hdm-stuttgart.de
* Leon Schröder ls066@hdm-stuttgart.de

Betreuende Professorin:  
[Prof. Dr. Barbara Dörsam](http://www.hdm-stuttgart.de/~doersam/)

Link zum Referenz-Projekt: https://github.com/schreon/morepeople-android  
Link zum SonarQube-Dashboard des Projekts: http://109.230.231.200:9000/dashboard/index/morepeopleandroid

#Inhalt
* [Einleitung](#einleitung)
* [Methodik](#methodik)
  * [Einrichtung von Android Studio](#einrichtung-von-android-studio)
  * [Versionskontrolle mit Git](#versionskontrolle-mit-git)
    * [Methode 1: Nutzung von Github](#methode-1-nutzung-von-github)
    * [Methode 2: Einsatz eines eigenen Git Servers](#methode-2-einsatz-eines-eigenen-git-servers)
      * [Git-Repository erstellen](#git-repository-erstellen)
      * [SSH-Zugriff konfigurieren](#ssh-zugriff-konfigurieren)
  * [Architektur der Testumgebung](#architektur-der-testumgebung)
* [Praxis](#praxis)
  * [Komponententests](#komponententests)
    * [Beispiel-Test zum Prüfen von Komponenten mit Views](#beispiel-test-zum-prüfen-von-komponenten-mit-views)
    * [Beispiel-Test zum Prüfen von Komponenten mit Location-Services](#beispiel-test-zum-prüfen-von-komponenten-mit-location-services)
    * [Beispiel-Test zum Prüfen von Komponenten mit HTTP-Requests](#beispiel-test-zum-prüfen-von-komponenten-mit-http-requests)
  * [Integrationstests](#integrationstests)
    * [setUp für Robotium](#setup-für-robotium)
    * [Triviales Beispiel eines Integrationstests](#triviales-beispiel-eines-integrationstests)
    * [Komplexes Beispiel eines Integrationstests](#komplexes-beispiel-eines-integrationstests)
* [Diskussion](#diskussion)
* [Fazit](#fazit)
* [Quellen und Referenzen](#quellen-und-referenzen)


Einleitung
==========

Die technische Qualität eines Software-Produkts lässt sich aus Sicht des Kunden und der Management-Ebene nur schwer bemessen. Dies liegt unter anderem daran, dass das zu entwickelnde Produkt nicht fassbar ist und sich Qualitätskriterien – anders als bei greifbaren Produkten – nur in Form von abstrakten Merkmalen manifestieren. Da jedoch der Erfolg eines Software-Produkts stark von der Stabiliät und Zuverlässigkeit der implementierten Funktionalitäten abhängt, wurden Verfahren, Entwicklungsprozesse und Abläufe entwickelt, anhand derer sich die Software-Qualität bemessen und verbessern lässt. Diese Techniken lösen verschiedene Probleme der Software-Entwicklung und setzen deshalb an verschiedene Stellen des Entwicklungsprozesses an.

So lässt sich auf Projekt-Management-Ebene mit einem [agilen Vorgehen](http://de.wikipedia.org/wiki/Agile_Softwareentwicklung) – im Gegensatz zur Entwicklung nach dem [Wasserfallmodell](http://www.enzyklopaedie-der-wirtschaftsinformatik.de/wi-enzyklopaedie/lexikon/is-management/Systementwicklung/Vorgehensmodell/Wasserfallmodell) – eine deutlich bessere Synchronisation zwischen Kunden und Entwickler erreichen, da der Kunde und die Entwickler regelmäßig den aktuellen Stand besprechen. Im Zuge dieser Entwicklung haben sich weitere Techniken etabliert, wie zum Beispiel [Continuous Integration](http://www.extremeprogramming.org/rules/integrateoften.html), Continuous Build, Continuous Delivery. Durch das ständige Zusammenführen von Softwarecode verschiedener Teammitglieder und das anschließende Erstellen des finalen Produkts können Fehler oder Schwierigkeiten frühzeitig aufgedeckt und behoben werden. Techniken wie Test-Automatisierung, statische Code-Analyse und die Überprüfung der Einhaltung vordefinierter Coding Conventions ergänzen die modernen Projekt-Management-Verfahren und sollen für eine qualitativ hochwertige Software sorgen.

Dieses Dokument ist eine Anleitung zur Einrichtung einer Continuous Integration Umgebung für mobile Anwendungen auf Basis von Android, welche im Rahmen eines studentischen Projekts an der Hochschule der Medien Stuttgart entstanden ist. Im Fokus dieser Arbeit steht neben der Implementierung der Softwaretools außerdem die kritische Auseinandersetzung mit dem Einsatz und dem Arbeitsaufwand der eingesetzten Verfahren . Dabei sollen Hürden aufgezeigt und der tatsächliche Nutzen des [Testdriven Development (TDD)](http://c2.com/cgi/wiki?TestDrivenDevelopment) bewertet werden.

Oftmals werden einführende Beispiele für test-getriebene Entwicklung der Komplexität realer Anwendungen nicht gerecht. Daher behandeöt diese Anleitung auch nicht-triviale Test-Fälle, die beispielsweise asynchrone Abläufe, eine Datenbank und Services von Drittanbietern mit einbeziehen. Die Beispiele beziehen sich auf ein studentisches Referenz-Projekt, welches parallel an der Hochschule der Medien entstanden ist. Dabei handelt s sich um [morepeople](https://github.com/schreon/morepeople-android), einem location-based Service in Form einer Android App. Mit morepeople können spontane Aktivitäten gestartet werden, für die automatisch interessierte Teilnehmer aus der Umgebung gesucht werden. Die Applikation erstreckt sich über einen Client- (Java/Android-Code) und einen Server-Teil (Python-Code). Anhand dieses Projekts, das ebenfalls auf Github zur Verfügung steht, sollen die eingesetzten Techniken erläutert werden. Dies hat den Vorteil, dass die Einrichtung und Verwendung einer automatisierten Testumgebung anhand eines realen Projekts Schritt für Schritt nachvollziehbar ist. 

Ergänzend zu dem Referenz-Projekt "morepeople" enthält dieses Repository eine Vorlage für eine vollständige Testumgebung inklusive Test-Schablonen für Komponenten- und Integrationstests. Die Projekt-Struktur ist auf Android Studio ausgelegt und kann als Basis für neue Projekte dienen.

Einrichtung der technischen Infrastruktur
=====================================

Im Folgenden wird umfassend beschrieben, wie die konkrete technische Infrastruktur für eine Continuous Integration Umgebung eingerichtet werden kann.

##Einrichtung von Android Studio

Um in einem Softwareprojekt, das von einem Team umgesetzt wird, von den eingesetzten Techniken zur Verbesserung der Qualität profitieren zu können, ist es sinnvoll, sich auf eine gemeinsame Entwicklungsumgebung zu einigen und ein Build-Tool einzusetzen. In dem studentischen Softwareprojekt kommt [Android Studio](http://developer.android.com/sdk/installing/studio.html) zum Einsatz, als Build-Tool wird [Gradle](http://www.gradle.org/) eingesetzt.

Anders als [Maven](http://maven.apache.org/) setzt Gradle nicht auf eine XML-basierte Konfiguration, sondern benutzt eine auf [Groovy](http://groovy.codehaus.org/) basierende Domain-Specific Language (DSL).

Das Buildfile `build.gradle` befindet sich direkt im Projektverzeichnis. Daneben liegt ein Ordner `app`. In diesem befindet sich der Code des Projekts im `src`-Verzeichnis. In diesem widerum ist der Applikationscode im Verzeichnis `main`, die Komponententests im Verzeichnis `test` und die Integrationstests im Verzeichnis `robotium`.

Die (vereinfachte) Ordnerstruktur des Projekts:

```
build.gradle
app/
  debug/
  main/
  src/
    main/
    test/
    robotium/
```

##Versionskontrolle mit Git

Die Basis für eine automatisierte Testumgebung liefert ein Versionskontrollsystem. Bei diesem Testprojekt fiel die Wahl auf [Git](http://git-scm.com/), da es sich sehr gut mit den weiteren Softwarequalitätstools verbinden lässt.
Grundsätzlich hat man für die Einrichtung einer automatisierten Testumgebung auf Basis von Git zwei Möglichkeiten:

1. Man nutzt einen Service (z.B. GitHub), über den man das Git-Repository bezieht.
1. Das Repository wird selbst gehostet. Man richtet einen Post-Receive Hook ein.

###Methode 1: Nutzung von Github

Zunächst muss ein Repository bei Github erstellt werden und alle beteiligten Entwickler als Collaborators eingetragen werden. Anschließend können alle Entwickler das Repository auf ihrem Entwicklerrechner klonen:

``` sh
$ git clone https://github.com/friedolinfoerder/android-test.git
```

###Methode 2: Einsatz eines eigenen Git Servers

Bei dieser Methode hostet man einen eigenen Git Server. Da git die Möglichkeit des [Einrichtens von Hooks](http://git-scm.com/docs/githooks) mitbringt, lässt sich so relativ einfach eine Automatisierung nach jedem Push auf den Server erreichen. Die benötigten Schritte zur Einrichtung eines git Servers und Anlegen eines Hooks zur Automatisierung sind im Folgenden beschrieben. 

####Git-Repository erstellen

Um ein git-Repository zu erstellen, müssen folgenden Schritte ausgeführt werden:

1. Sich mit dem Server über SSH verbinden: `$ ssh root@serverip`
1. git auf dem Server installieren: `$ apt-get install git`
1. Neuen User “git” anlegen: `$ adduser git`
1. Ordner für Repositories anlegen (Das /home/git Verzeichnis des git-Users dient als Ablage für die git-Repositories): `$ mkdir /home/git`
1. Zum Ordner wechseln: `$ cd /home/git`
1. Das Repository erstellen: `$ git init --bare testprojekt`

Zusammengefasst die Schritte 1-6:

``` sh
$ ssh root@serverip
$ apt-get install git
$ adduser git
$ mkdir /home/git
$ cd /home/git
$ git init --bare testprojekt
```

####SSH-Zugriff konfigurieren
Der Zugriff auf das neu erstellte Repository erfolgt mittels SSH. Dafür wird für den git-User SSH konfiguriert:

``` sh
$ ssh git@serverip
$ mkdir .ssh
``` 

Damit nun jeder Entwickler Zugriff auf das neu erstellte Repository erhält, muss anschließend jeder Entwickler ein private/public-Schlüsselpaar erstellen:

``` sh
$ ssh-keygen -t dsa
```

Wurde der standardmäßige Dateiname gewählt, werden zwei Schlüssel generiert:

* `id_dsa` , der Private-Key, geschützt durch das gewählte Passwort
* `id_dsa.pub` , der Public-Key, welcher auf den Server übertragen werden muss

Alle Entwickler, die an den Projekten mitarbeiten sollen, müssen ihren Public-Key bereitstellen.
Die Public-Keys aller Entwickler müssen daraufhin auf den Server übertragen werden.
Dies lässt sich vom Entwicklerrechner aus folgendermaßen bewerkstelligen:

``` sh
$ scp id_dsa.pub git@serverip:/.ssh/entwickler_name.pub
```

Falls der Key in einem anderen Format als OpenSSH ist, muss er konvertiert werden:

``` sh
$ ssh-keygen -i -f entwickler_name.pub  > entwickler_name.com.pub
```

Anschließend muss der Key eingetragen werden:

``` sh
$ cat entwickler_name.com.pub >> .ssh/authorized_keys
```
    
Im Anschluss muss nun der Entwickler, der das Projekt anlegt, einen initialen Commit erstellen und den Server festlegen. Dies lässt sich mit folgenden Kommandos durchführen:

``` sh
$ git add .gitignore
$ git commit -m’.gitignore added’
$ git add --all :
$ git commit -m’initial commit’
$ git remote add origin git@serverip:testprojekt
$ git push origin master
```

Nun können alle weiteren Entwickler das erzeugte Projekt klonen:

``` sh
$ git clone git@serverip:testprojekt
```

##Architektur der Testumgebung

Die Architektur der Testumgebung für das Test-Projekt hat sich im Laufe der Zeit ständig geändert. Dies liegt daran, dass ständig neue Tools ausprobiert wurden, die widerum Einfluss auf andere Komponenten hatten. Die finale Fassung ist relativ schlank und dadurch sehr übersichtlich. Dies wurde unter anderem dadurch erreicht, dass ein eigener Git Server eingerichtet wurde. Auch auf einen CI-Server wie [Jenkins](http://jenkins-ci.org/) oder [Travis](https://travis-ci.org/) wurde verzichtet, da alle wichtigen Analysen in der finalen Variante auch mit Gradle in Verbindung mit [SonarQube](http://www.sonarqube.org/) durchgeführt werden konnten.

Auf dem Git Server wurde ein Repository angelegt. Anschließend wurde ein **Post-Receive Hook** erstellt. Mit Hilfe dieses Hooks werden die Komponententests gestartet und anschließend SonarQube benachrichtigt, dass eine neue Applikationsversion vorhanden ist.
Der Hook muss in der Datei `post-receive` im Verzeichnis `hooks` des Repositorys angelegt werden. Im Test-Projekt ist in dieser Datei diese Zeilen vorhanden:

``` sh
#!/bin/sh
exec /home/morepeople/android/hooks/updaterepo.sh >&- 2>&- &
```

Diese Zeile bewirkt, dass das Shell-Script `updaterepo.sh` aufgerufen wird, wobei durch den Zusatz `>&- 2>&- &` das Script im Hintergrund läuft und so der Benutzer des Git-Repositorys keinerlei Output zu sehen bekommt und der Push sofort fertig ist. Die Datei `updaterepo.sh` enthält folgenden Inhalt:

``` sh
#!/bin/sh
tmpdir=/home/morepeople/tmp/android-$$
git clone /home/morepeople/android $tmpdir
cd $tmpdir
chmod +x gradlew
./gradlew clean test sonarRunner --continue
cd ..
rm -rf $tmpdir
```

Für jeden Push wird das aktuelle Repository in einen separaten Ordner geklont. Anschließend werden die Tasks `clean`, `test` und `sonarRunner` vom Gradle-Buildscript ausgeführt. Dies bewirkt, dass Berichte über Tests und Testabdeckung erstellt werden und anschließend SonarQube die Ergebnisse einliest, vorhandene Codefiles analysiert und daraufhin die Ergebnisse in die Datenbank schreibt. Das Resultat lässt sich über das Dashboard von SonarQube betrachten und enthält:

* Statische Codeanalysen
  * Zeilen an Code
  * Anzahl an Klassen
  * Dokumentation der Public-API
  * Anteil an Kommentaren
  * Anteil an Duplikationen
  * Komplexität des Codes
  * Schwachstellen
* Ergebnisse der Tests
* Testabdeckung

Visualisierung der Testumgebung:
<p align="center">
 <img alt="Visualisierung der Testumgebung" src="http://friedolinfoerder.github.io/repos/android-test/images/QA_Workflow.png" />
</p>

Praxis
======

Im Folgenden wird erklärt, wie Komponenten- und Integrationstests im Test-Projekt geschrieben wurden.
    
##Komponententests

Bevor ein Entwickler Änderungen am Source Code an den git-Server pusht, sollten eigentlich sämtliche Tests durchlaufen werden, um zu garantieren, dass der Haupt-Entwicklungszweig lauffähig bleibt. Dauert das Durchlaufen der Tests zu lange, besteht allerdings die Gefahr, dass das Testsystem als Hürde angesehen wird.

Auch für die test-getriebene Software-Entwicklung auf den lokalen Entwickler-Systemen ist es wichtig, dass die Tests in akzeptabler Zeit ablaufen, um nicht den Arbeitsfluss der Mitarbeiter zu stören.

Klassischerweise laufen die Tests in Android-Projekten auf einem physisch vorhandenen Endgerät oder einem Emulator statt. Der modifizierte Source Code wird kompiliert und auf das Gerät geladen, was allein schon mehrere Minuten Zeit in Anspruch nehmen kann.

Um dies zu vermeiden, wurde die Library [Robolectric](http://robolectric.org/) eingeführt. Damit wird es möglich, Android-Code in der Java Virtual Machine (JVM) auszuführen und so innerhalb kurzer Zeit die Test-Ergebnisse zu erhalten, ohne dass zuvor ein Gerät oder ein Emulator gestartet werden muss. Auch das automatisierte Testen auf einem Server wird hierdurch möglich. Zudem können dadurch Werkzeuge, die ursprünglich nur für Java-Anwendungen entwickelt wurden, auch auf Android-Projekte angewandt werden.

Realisiert wird dies, indem alle Android-spezifischen Zugriffe abgefangen und an Robolectric weitergeleitet werden. Um sich beispielsweise ein Activity-Objekt zu instanziieren und dieses zu testen, muss folgende Zeile ausgeführt werden:

``` java
Activity activity = Robolectric.buildActivity(WelcomeActivity.class).create().get();
```

Robolectric ist dem originalen Android-Framework im Kontext von Komponententests nicht nur was die Rechenzeit angeht überlegen. Mit Robolectric kann der vollständige Gerätezustand manipuliert bzw. simuliert werden. So kann über einen Robolectric-Tests beispielsweise definiert werden, wie sich die Applikation verhalten soll, wenn während eines Vorgangs die Internet-Verbindung abbricht. 

Alle Geräte-Funktionen lassen sich über sogenannte *Shadow Objects* simulieren. Diese sind vergleichbar mit Mocks oder Stubs, wie man sie auch vom Testen her kennt, jedoch bieten die *Shadow Objects* noch zusätzliche Methoden an, über die man das zu testende Objekt untersuchen oder verändern kann.

Robolectric wurde in das Gradle-Build-Script als Abhängigkeit aufgenommen. Bei jedem Push auf den Server werden die Komponententests ausgeführt und die Ergebnisse im Anschluss via SonarQube visualisiert. Die Entwickler können die Tests auch lokal auf ihrem System über die Konsole ausführen:


``` sh
$ ./gradlew test
```

Schon auf dem Entwickler-Rechner selbst wird dabei eine kurze Ergebnis-Übersicht generiert. Sie liegt im Verzeichnis der Form `build/test-report/debug/packages/<package-name>.html`. Die Ergebnisse können dann über einen beliebigen Web-Browser betrachtet werden:

<p align="center">
 <img alt="Gradle Results" src="http://friedolinfoerder.github.io/repos/android-test/images/gradle_results.png" />
</p>

### Beispiel-Test zum Prüfen von Komponenten mit Views

Hierzu ein Beispiel aus der Klasse [`ChatActivityTest`](https://github.com/schreon/morepeople-android/blob/master/app/src/test/java/morepeople/android/app/ChatActivityTest.java):

``` java
// if a user sends a chat message, it should appear in the textbox
@Test
public void shouldDisplayNewMessages() {
    ListView chatHistoryView = (ListView) activity.findViewById(R.id.chat_history);
    assertNotNull(chatHistoryView);

    ChatAdapter chatAdapter = activity.getChatAdapterAdapter();

    for (int m=0; m < 100; m++) {
        // new message arrives
        String testMessage = "message" + m;
        chatAdapter.addNewMessage(testMessage);

        // update robolectric
        Robolectric.shadowOf(chatHistoryView).populateItems();

        // child history view should have at least 1 child now
        assertTrue(chatHistoryView.getChildCount() > 0);

        boolean isDisplayed = false;
        TextView messageView = null;
        String displayedMessage = null;
        // the ListView should now display the new message
        for (int i=0; i < chatHistoryView.getChildCount(); i++) {
            messageView = (TextView)chatHistoryView.getChildAt(i);
            assertNotNull(messageView);
            displayedMessage = messageView.getText().toString();
            if (testMessage.equals(displayedMessage)) {
                isDisplayed = true;
            }
        }
        assertNotNull(messageView);
        assertNotNull(displayedMessage);
        assertTrue(isDisplayed);
    }
}
```

In diesem Komponententest wird überprüft, ob die `chatHistoryView` tatsächlich erneuert wird, sobald dem `chatAdapter` eine neue Nachricht hinzugefügt wird. Hierzu werden zunächst die entsprechenden Methoden der beiden Komponenten aufgerufen. Anschließend werden die Kind-Elemente der `ListView` durchsucht. Assert-Bedingung ist, dass die neue Nachricht in der `ListView` vorkommt. Dies wird 100 mal wiederholt, um zu garantieren, dass neue Nachrichten auch dann angezeigt werden, wenn die Liste im `chatAdapter` größer wird, als sie in der `chatHistoryView` angezeigt werden kann. Bevor die Kind-Elemente der `chatHistoryView` durchlaufen werden können, muss zunächst Robolectric verwendet werden, um die Views zu aktualisieren: `Robolectric.shadowOf(chatHistoryView).populateItems();`.

### Beispiel-Test zum Prüfen von Komponenten mit Location-Services

Dieser Test hat das Ziel, die im Rahmen des Projekts entwickelte Komponente `LocationWrapper` zum Ansteuern der Geo-Location zu testen. Die asynchrone Natur dieser Komponente erfordert ein komplexeres Test-Verfahren, welches nach dem kompletten Code-Auszug aus der Klasse [`LocationWrapperTest`](https://github.com/schreon/morepeople-android/blob/master/app/src/test/java/morepeople/android/app/LocationWrapperTest.java) detailliert dargelegt wird. 

``` java
@Test
public void shouldProvideNewLocation() throws InterruptedException {
    // The asynchronous nature of LocationWrapper requires an object where assertion results
    // can be stored. This is necessary, because it could not be determined if an event
    // that should be reached (and thus does not fail) has actually been reached.
    final Map<String, Boolean> assertionMap = new HashMap<String, Boolean>();

    // During robolectric tests, no real device is available. Thus, system services like
    // the NETWORK_PROVIDER and the according LocationManager must be mocked.
    LocationManager instanceOfLocationManager = (LocationManager) Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
    ShadowLocationManager shadowLocationManager = shadowOf(instanceOfLocationManager);
    shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

    final Location currentLocation = new Location(LocationManager.NETWORK_PROVIDER);
    currentLocation.setLongitude(123);
    currentLocation.setLatitude(456);
    // Attention: Android won't trigger a changed location event if the time span between
    // the location objects is too small!!!
    currentLocation.setTime(0);

    final Location newLocation = new Location(LocationManager.NETWORK_PROVIDER);
    newLocation.setLongitude(666);
    newLocation.setLatitude(333);
    // Attention: Android won't trigger a changed location event if the time span between
    // the location objects is too small!!!
    newLocation.setTime(1000000);

    shadowLocationManager.setLastKnownLocation(LocationManager.NETWORK_PROVIDER, currentLocation);

    LocationResponseHandler locationResponseHandler = new LocationResponseHandler(){
        @Override
        public void gotInstantTemporaryLocation(Location location) {
            assertNotNull(location);
            assertEquals(currentLocation, location);
            assertionMap.put("gotInstantTemporaryLocation", true);
        }

        @Override
        public void gotFallbackLocation(Location location) {
            // Should not get here!
            fail();
        }

        @Override
        public void gotNewLocation(Location location) {
            assertNotNull(location);
            assertEquals(newLocation, location);
            assertionMap.put("gotNewLocation", true);
        }
    };

    // This starts the asynchronous location request
    locationWrapper.requestLocation(activity.getBaseContext(), locationResponseHandler, 60000);

    // This simulates a location changed event
    shadowLocationManager.simulateLocation(newLocation);

    // Wait for all async tasks to finish
    Robolectric.runUiThreadTasks();

    // Assert that the correct asynchronous event handlers have been called
    assertTrue(assertionMap.keySet().contains("gotInstantTemporaryLocation"));
    assertTrue(assertionMap.keySet().contains("gotNewLocation"));

    // Immediately run the delayed fallback task if it still is existent
    Robolectric.runUiThreadTasksIncludingDelayedTasks();
}
```

Zunächst wird ein Behelfs-Objekt instanziiert, welches dazu dient, asynchron entstehende Test-Ergebnisse einzusammeln:
``` java
final Map<String, Boolean> assertionMap = new HashMap<String, Boolean>();
```
Da zur Test-Laufzeit der Robolectric-Tests kein wirkliches Device zur Verfügung steht, müssen bestimmte Services gemockt werden, um die Funktionstüchtigkeit der Komponenten zu prüfen. Hierzu bietet Robolectric die Möglichkeit, Shadow-Objects zu erzeugen. In den folgenden Code-Zeilen wird eine Instanz eines `LocationManager` zusammen mit dem zugehörigen `ShadowLocationManager` erzeugt und konfiguriert:

``` java
LocationManager instanceOfLocationManager = (LocationManager) Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
ShadowLocationManager shadowLocationManager = shadowOf(instanceOfLocationManager);
shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
```

Im nächsten Schritt werden `Location` Objekte erzeugt. `currentLocation` stellt die bisherige Position des Smartphones da, `newLocation` beinhaltet Uhrzeit, Längen- und Breitengrade der veränderten/neuen Position. Hierbei ist zu beachten, dass Android nur dann Veränderungs-Ereignisse bezüglich der Location feuert, wenn die Zeitdifferenz und der räumliche Abstand zwischen den Locations groß genug ist. In Tests sollten daher ausreichend abweichende Location Objekte erzeugt werden.

``` java
final Location currentLocation = new Location(LocationManager.NETWORK_PROVIDER);
currentLocation.setLongitude(123);
currentLocation.setLatitude(456);
currentLocation.setTime(0);

final Location newLocation = new Location(LocationManager.NETWORK_PROVIDER);
newLocation.setLongitude(666);
newLocation.setLatitude(333);
newLocation.setTime(1000000);
```

Als nächstes wird im `LocationManager` die zuletzt bekannte Position auf `currentLocation` gesetzt:

``` java
shadowLocationManager.setLastKnownLocation(LocationManager.NETWORK_PROVIDER, currentLocation);
```

Da die Anfrage der neuen Geo-Koordinaten asynchron abläuft, wird ein `LocationResponseHandler` benötigt. Dies hat den Nebeneffekt, dass auch diese im Rahmen des Projekts entstandene Komponente getestet wird. 

``` java
LocationResponseHandler locationResponseHandler = new LocationResponseHandler(){
    @Override
    public void gotInstantTemporaryLocation(Location location) {
        assertNotNull(location);
        assertEquals(currentLocation, location);
        assertionMap.put("gotInstantTemporaryLocation", true);
    }

    @Override
    public void gotFallbackLocation(Location location) {
        // Should not get here!
        fail();
    }

    @Override
    public void gotNewLocation(Location location) {
        assertNotNull(location);
        assertEquals(newLocation, location);
        assertionMap.put("gotNewLocation", true);
    }
};
```

Der `gotInstantTemporaryLocation` Callback sollte in jedem Fall durchgeführt werden - er liest hier nur die `lastKnownLocation` aus. Diese sollte natürlich identisch mit der zuvor gesetzten `currentLocation` sein. Um dies zu prüfen, wird der `assertionMap` ein entsprechender Eintrag hinzugefügt.

Die Methode `gotFallbackLocation` ist für den Fall gedacht, dass das Smartphone nicht in der Lage ist, die Position innerhalb der vorgegebenen Zeitspanne zu bestimmen. In dem Fall wird einfach erneut die letztbekannte Location verwendet. Da dieser Test-Fall den Fall prüft, dass sich die Location tatsächlich ändert, darf dieser Zweig eigentlich nicht erreicht werden. Um einen Fehler zu provozieren, wird hier dauer `fail()` aufgerufen.

Letztlich definiert der Code in `gotNewLocation` das erwartete Verhalten, welches im Fall einer veränderten Location ablaufen soll. Erneut wird das Ergebnis der Prüfung in die `assertionMap` eingetragen.

Die folgende Zeile registriert den oben dargelegten `LocationResponseHandler`, und der `LocationWrapper` beginnt auf Veränderungen sowohl des `NETWORK_PROVIDER` als auch des `GPS_PROVIDER` zu lauschen:

``` java
locationWrapper.requestLocation(activity.getBaseContext(), locationResponseHandler, 60000);
```

Die folgende Zeile löst nun das Ereignis einer veränderten Location aus:

``` java
shadowLocationManager.simulateLocation(newLocation);
```

Da Robolectric Tests grundsätzlich single-threaded laufen, kann es eigentlich keine Race-Conditions geben. Dennoch liegen die asynchronen Tasks in einem Stack. Um zu garantieren, dass die anstehenden UI-Tasks durchgelaufen sind, bevor die `assertionMap` geprüft wird, sollte der folgende Befehl ausgeführt werden:

``` java
Robolectric.runUiThreadTasks();
```

Letztlich kann die `assertionMap` geprüft werden:

``` java
assertTrue(assertionMap.keySet().contains("gotInstantTemporaryLocation"));
assertTrue(assertionMap.keySet().contains("gotNewLocation"));
```        

Robolectric erlaubt es, verzögerte Tasks, die z.B. via `postDelayed` gestartet wurden, sofort durchzuführen. Auf die Weise kann geprüft werden, ob die Methode `gotFallbackLocation` tatsächlich niemals aufgerufen wird - obwohl dies eigentlich erst in 60 Sekunden der Fall wäre:

``` java
Robolectric.runUiThreadTasksIncludingDelayedTasks();
```
Wäre der entsprechende asynchrone Task zu `gotFallbackLocation` noch immer angemeldet, dann würde jetzt aufgrund des oben genannten `fail()` Statements eine Exception ausgelöst werden.


``` java
locationWrapper.requestLocation(activity.getBaseContext(), locationResponseHandler, 60000);
```

### Beispiel-Test zum Prüfen von Komponenten mit HTTP-Requests

Komplexere mobile Anwendungen beinhalten oftmals Komponenten, welche via Netzwerk auf einen oder mehrere Server zugreifen müssen. Robolectric erlaubt es, solche Komponenten zu testen, obwohl gar keine echte Verbindung zu einem entsprechenden Server besteht. Dies geschieht über einen Stack an vorbereiteten HTTP-Antworten, der vor dem jeweiligen Test angefertigt werden muss. 

Im morepeople Projekt wird nach jedem Applikations-Start eine Anfrage an den Server geschickt. In der Antwort befindet sich die Information darüber, welchen Zustand der Client gerade hat. Anhand des Zustands kann der Client dann die entsprechende `Activity` auswählen und starten.

Um dies in den Robolectric-Tests zu ermöglichen, wird noch vor der `setUp` Methode folgende Initialisierungs-Abfolge durchgeführt:

``` java
@BeforeClass
public static void setUpEnvironment() {
    MainApplication.preInit = new Runnable() {
        @Override
        public void run() {
            // Insert registration id and the user name into SharedPreferences
            SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("MorePeople", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("appUsername", "Thorsten Test").commit();
            sharedPreferences.edit().putString(MainRegistrar.PROPERTY_REG_ID, "test-gcm-id").commit();

            // Add pending HTTP response which will be served as soon as the application
            // sends the first HTTP request (no matter which request that will be).
            Robolectric.addPendingHttpResponse(200, "{ 'STATE' : '"+MainApplication.UserState.OFFLINE.toString()+"' }");
        }
    };
}
```

In der `MainApplication` Klasse wurde die `onCreate` Methode insofern erweitert, dass aus der Robolectric-Test-Umgebung heraus Anweisungen injiziert werden können, die zwar eine fertig instanziierte `Application` benötigen, also nach der `super.onCreate()`, aber noch vor den restlichen applikationsspezifischen Initialisierungs-Schritten ablaufen müssen:

``` java
@Override
public void onCreate() {
    super.onCreate();

    if (preInit != null) {
        preInit.run();
    }

    init();
}
```

Innnerhalb der applikationsspezifischen Initialisierungsschritten (in `init()`) wird dann der HTTP-Request abgesetzt, welcher durch Robolectric sofort abgefangen wird. In diesem Fall wäre die Antwort ein JSON-String mit dem Inhalt `{ 'STATE' : 'OFFLINE' }`.


##Integrationstests

Für die Integrationstest kam [Robotium](https://code.google.com/p/robotium/) zum Einsatz. Mit Robotium ist es möglich, Black-Box Tests über mehrere Activities hinweg zum erstellen, die über das User Interface Funktionalitäten prüfen. Dies steht dem manuellen Testen von Anwendungen in nichts nach; zusätzlich besteht der große Vorteil, dass die angelegten Tests automatisiert durchlaufen und jederzeit wiederholt werden können. Somit kann die korrekte Funktionsweise der App aus Nutzersicht überprüft werden.
Robotium bietet eine [einfach zu erlernende Api](http://robotium.googlecode.com/svn/doc/index-all.html) zum Erstellen der Testfälle. Darüber wird es beispielsweise möglich, Textfelder auszufüllen, Buttons zu drücken oder auf das Öffnen von anderen Activities zu warten.

Robotium wurde in die Testumgebung integriert. Anders als bei den Komponententests werden die Integrationstests jedoch nicht bei jedem Push auf den Server automatisch ausgeführt, sondern können mit diesem Kommando über Gradle gestartet werden:

``` sh
$ ./gradlew robotium
```

###setUp für Robotium
Auch die Robotium tests basieren auf [JUnit](http://junit.org/) Testfällen. Dementsprechend wird in der setUp Methode der Zustand des Test-Servers zurückgesetzt und ein sogenanntes `Solo` Objekt erstellt. Die `Solo`-Klasse dient zum Ansteuern der Activities innerhalb von Robotium-Tests.

``` java
@Override
protected void setUp() throws Exception {
    super.setUp();

    Log.d("robotium", "reset test server state.");
    doGetRequest("/reset");

    Log.d("robotium", "creating solo");
    solo = new Solo(getInstrumentation(), getActivity());
}
```
Die hier gezeigte `setUp`-Methode wird den folgenden Beispielen vorausgesetzt.
Die Folgenden Beispiele sowie die `setUp`-Methode befinden sich in der [`SearchEnvironmentTest`](https://github.com/schreon/morepeople-android/blob/master/app/src/robotium/java/morepeople/android/app/SearchEnvironmentTest.java)-Klasse

### Triviales Beispiel eines Integrationstests

Im folgenden Test wird der Server zurückgesetzt, die App wird gestartet und es wird gewartet bis die `SearchActivity` sichtbar ist. Anschließend wird geprüft, ob die Übersichts-Liste leer ist.

``` java
public void testNoItemsInListIfResetted() throws Exception {
    Log.d("robotium", "testNoItemsInListIfResetted");

    Log.d("robotium", "wait, until the search activity shows up");
    solo.waitForActivity(SearchActivity.class);

    Log.d("robotium", "get the listview");
    ListView listView = solo.getCurrentViews(ListView.class).get(0);

    Log.d("robotium", "test that the list view is empty");
    assertEquals(0, listView.getChildCount());
}

```

### Komplexes Beispiel eines Integrationstests

In diesem Beispiel wird getestet, ob sich beim Klicken auf ein existierendes Listenelement ein Dialog öffnet. Anschließend wird geprüft, ob sich dieser Dialog über den Abbrechen-Button schließen lässt.

``` java
public void testSearchAndClickAndCancel() throws Exception {
    Log.d("robotium", "testSearchAndClickAndCancel");

    Log.d("robotium", "add test user");
    addTestUser("bier");

    Log.d("robotium", "wait, until the search activity shows up");
    solo.waitForActivity(SearchActivity.class);

    Log.d("robotium", "get the listview");
    final ListView listView = solo.getCurrentViews(ListView.class).get(0);

    Log.d("robotium", "wait for listview to populate, with a timeout of 60 seconds");
    solo.waitForCondition(new Condition() {
        @Override
        public boolean isSatisfied() {
            return listView.getChildCount() > 0;
        }
    }, 60000);

    Log.d("robotium", "assert that exactly 1 entry is in listview");
    assertEquals(1, listView.getChildCount());

    Log.d("robotium", "click first item in the list");
    solo.clickInList(0);

    Log.d("robotium", "wait for the confirmation dialog to appear");
    solo.waitForDialogToOpen();

    Log.d("robotium", "click on the cancel button");
    solo.clickOnButton("Nein");

    Log.d("robotium", "wait for the dialog to close");
    solo.waitForDialogToClose();
}

```

Mit `solo.getCurrentViews(ListView.class).get(0);` wird zunächst die Liste geholt und im Anschluss unter Einsatz der Methode `waitForCondition` solange gewartet, bis mindestens ein Element in der Liste ist. Danach wird geprüft, ob wirklich genau ein Listeneintrag vorhanden und daraufhin mit `solo.clickInList(0)` auf dieses Element geklickt.
Nun soll sich ein Dialog öffnen, der mit einem Klick auf den Button mit dem Text `Nein` wieder geschlossen werden soll. Die Methoden `waitForDialogToOpen`, `clickOnButton("Nein")` und `waitForDialogToClose` prüfen diesen Ablauf.


Diskussion
==========
In diesem Testprojekt wurde eine Lösung geschaffen, mit der man die Qualität eines Android-Projekts deutlich steigern kann. Dies wurde in erster Linie durch das Bereitstellen einer visuellen Dashboards (Sonarqube) erreicht, das zu jeder Zeit den aktuellen Stand des Softwareprodukts widerspiegelt. Neben statischen Analysen liefert dieses auch Informationen zu den durchgelaufenen Tests und zur Testabdeckung. Dieses Setup bietet einen deutlichen Mehrwert, da es neben den Informationen auch jederzeit eine Motivation für die Entwickler bietet: Es liefert einen Anreiz, qualitativ hochwertigeren Code mit besserer Codeabdeckung, weniger Komplexität und mehr Dokumentation zu schreiben, da die Ergebnisse auf dem Dashboard sofort sichtbar sind und man auch die Entwicklung zu vorherigen Softwareständen begutachten kann. 
Das Einrichten dieses Systems war jedoch mit sehr vielen Schwierigkeiten verbunden. Dies hatte mehrere Gründe. Zum einen gibt es im Themengebiet Android-Testautomation relativ wenige Dokumentation – anders als man es in Hinblick auf dieses wichtige Thema vermuten würde. Gerade, wenn man eine gesamte automatisierte Testumgebung aufbauen möchte, gibt es zwar zu den einzelnen Tools/Werkzeuge nützliche Informationen und Dokumentationen, jedoch fehlt oft die Beschreibung, wie man diese mit anderen Tools kombiniert bzw. in andere Tools integriert. Um das reibungslose Zusammenspiel von Android Studio, Android SDK, Git, Jenkins, Sonarqube, Sonarqube-Runner, Gradle, Maven, Jacoco, Robolectic, Robotium und verschiedenen Programmier-Bibliotheken zu erreichen, musste im Laufe der Einrichtung der Testumgebung oftmals die Strategie gewechselt und die System-Konfiguration verändert werden. Folgende Möglichkeiten wurden im Laufe des Projekts ausprobiert und durch andere Methoden ersetzt:

* Unabhängige Projekte für den Entwicklungs- und Testcode. Verwaltung der Projekte mit Maven.
Statt Maven wurde Gradle eingesetzt. Dadurch lassen sich Entwicklungs- und Testcode in einem Projekt verwalten.
* [Eclipse](https://www.eclipse.org/) als IDE. Umstellung auf Android Studio. Android Studio unterstützt von Haus aus das Entwickeln in Zusammenarbeit mit Gradle-Buildfiles.
* Testen auf dem Server mit virtuellem Device. Umstellung auf Robolectric.
Durch Robolectric braucht man für das Ausführen der Tests keine Android-Geräte oder Emulatoren mehr. Dadurch hat die Laufzeit der Komponententests deutlich abgenommen.
* Manuelle Integrationstest mit JUnit. Umstellung auf semi-automatisierte Integrationstests mit Robotium.
Robotium liefert die Möglichkeit, auf UI-Ebene zu testen und bietet dabei eine einfache API an.
* Beziehen eines Git-Repositorys über GitHub. Umstellung auf eigenes Git-Repositories durch die Einrichtung eines Git-Server. Sonarqube lässt sich so einfacher ansprechen.
* Einsatz von Jenkins als CI-Server. Umstellung auf Travis CI, da dies eine Integration mit GitHub bietet. Zuletzt wurde auf externe CI-Server verzichtet, da dieser selbst via git, SonarQube und eigenen Shell-Scripten realisiert wurde.

Aufgrund dieser vielen Anpassung ergab sich ein enormer Aufwand. Doch gerade durch die vielen Änderungen entstand zuletzt ein System, das die wichtigsten Analysen enthält, konsistent aufgebaut ist (alle relevanten Konfigurationen sind im Gradle-File enthalten) und das trotzdem übersichtlich und wartbar bleibt. Sollten im Laufe der Zeit weitere Automationen notwendig sein, kann beispielsweise auch noch ein CI-Server wie Jenkins in die Kette der Tools aufgenommen werden. Die vorgestellte Testumgebung um das Gradle-Buildtool erlaubt dies ohne Einschränkungen.

Fazit
=====
Test-Driven Development ist anfangs gewöhnungsbedürftig. Erst einen Test zu schreiben und dann die Implementierung anzufertigen zwingt den Entwickler, sich von vorneherein mit der Dokumentation der verwendeten Programmiersprache und der verwendeten Frameworks auseinander zu setzen. Dies erweckt zunächst den Eindruck, nicht voranzukommen oder unproduktiv zu sein. Jedoch zahlt sich der initiale Mehraufwand im späteren Projektablauf aus, denn ohne Tests entsteht unweigerlich die Situation, dass komplexe Fehler/Bugs auftreten und ein signifikanter Zeitaufwand für eine nicht immer zielführende Fehlersuche anfällt. TDD führt implizit dazu, dass man den Source Code leicht testbar hält - was im Umkehrschluss meistens bedeutet, dass er modularer wird und die Komponenten saubere Schnittstellen haben. 

Ist diese anfängliche motivationsbedingte Hürde überwunden, kommt eine professionelle Continuous Integration Umgebung voll zum Tragen. Metriken wie Code Coverage, Dokumentations-Abdeckung, Code Complexity, etc wirken sehr motivierend, da sie den Projektfortschritt bezüglich der Code Qualität visualisieren. 

Die größte Herausforderung im Rahmen dieses Projekts bestand allerdings in der Einrichtung der Infrastruktur. Hierzu dient nun diese Anleitung und die in diesem Repository verfügbare Projekt-Vorlage, sodass der Fokus in zukünftigen Android-Projekten gleich auf der eigentlichen Entwicklung und dem test-getriebenen Entwickeln liegen kann.


Quellen und Referenzen
======================

1. Android Developers http://developer.android.com/index.html
2. Android Studio http://developer.android.com/sdk/installing/studio.html
2. Gradle http://www.gradle.org/
3. Maven http://maven.apache.org/
4. JUnit http://junit.org/
4. Robolectric http://robolectric.org/index.html
5. Robotium http://code.google.com/p/robotium/
6. SonarQube http://www.sonarqube.org/
7. Git http://git-scm.com/
8. GitHub https://github.com/
10. Ubuntu http://www.ubuntu.com/
