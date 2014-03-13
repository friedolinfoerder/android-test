Test Projekt
============

Die Qualität eines Produkts lässt sich im Umfeld von IT-Projekten schwer bemessen. Dies liegt unter anderem daran, dass das zu entwickelnde Produkt vom Kunden nicht fassbar ist und sich das Aufstellen von Qualitätskriterien – anders als bei …(anfassbaren) Produkten – in den meisten Fällen als schwierig erweist. Da jedoch der Erfolg eines Softwareprodukts stark von der Stabiliät und Zuverlässigkeit der implementierten Funktionalitäten abhängt, wurden Verfahren, Entwicklungsprozesse und Abläufe entwickelt, die eine erhöhte Softwarequalität mit sich bringen sollen. Diese Techniken lösen verschiedene Probleme der Softwareentwicklung und setzen deshalb an verschiedene Stellen des Entwicklungsprozesses an. So lässt sich mit einem agilen Vorgehen – im Gegensatz zur Entwicklung nach dem [Wasserfallmodell](http://www.enzyklopaedie-der-wirtschaftsinformatik.de/wi-enzyklopaedie/lexikon/is-management/Systementwicklung/Vorgehensmodell/Wasserfallmodell) – eine deutlich bessere Synchronisation zwischen Kunden und Entwickler erreichen, da der Kunde und die Entwickler regelmäßig den aktuellen Stand besprechen. Im Zuge dieser Entwicklung haben sich weitere Techniken etabliert, wie zum Beispiel Continuous Integration, Continuous Build, Continuous Delivery. Durch das ständige Zusammenführen von Softwarecode verschiedener Teammitglieder und das anschließende Erstellen des finalen Produkts können Fehler oder Schwierigkeiten sofort aufgedeckt und behoben werden. Techniken wie Testautomatisierungen, statische Codeanalysen und die Überprüfung der Einhaltung vordefinierter Coding Conventions ergänzen die Verfahren und sollen für eine qualitativ hochwertige Software sorgen.

Ziel dieses Testprojekts war es, die gängigen Verfahren zur Verbesserung der Softwarequalität anhand eines studentischen Projektes zu untersuchen, die für dieses Projekt sinnvollen Softwaretools auszuwählen und diese anschließend in den Entwicklungsprozess zu integrieren.
Neben der Implementierung der Softwaretools steht außerdem die kritische Auseinandersetzung mit dem Einsatz und Aufwand eingesetzter Verfahren im Fokus dieser Arbeit. Dabei sollen Schwierigkeiten aufgezeigt und der tatsächliche Nutzen des [Testdriven Development (TDD)](http://c2.com/cgi/wiki?TestDrivenDevelopment) evaluiert werden. 

Bei dem studentischen Projekt handelt es sich um eine Android App, die Personen mit den gleichen Interessen zusammenbringen soll. Für die Umsetzung ist ein Client- (Java/Android-Code) und ein Server-Teil (Python-Code) notwendig. Da die Darstellung der eingesetzten Techniken anhand des gesamten Softwareprojekts im Rahmen dieser Arbeit zu umfassend wäre, werden die Techniken anhand eines Beispielsprojekts erläutert. Dies bringt einen weiteren Vorteil mit sich: Studenten, die eine funktionierendes Test-Setup für Projekte benötigen, können mit Hilfe der Codebasis zu diesem Beispielprojekt, die auf Github zur Verfügung steht, Schritt für Schritt die Einrichtung und Verwendung einer automatisierten Testumgebung anhand eines überschaubaren (aber dennoch realistischen) Beispiels nachvollziehen.

#Einrichtung von Android Studio

Um in einem Softwareprojekt, das von einem Team umgesetzt wird, von den eingesetzten Techniken zur Verbesserung der Qualität profitieren zu können, ist es sinnvoll, sich auf eine gemeinsame Entwicklungsumgebung zu einigen und ein Build-Tool einzusetzen. In dem studentischen Softwareprojekt kommt [Android Studio](http://developer.android.com/sdk/installing/studio.html) zum Einsatz, als Build-Tool wird [Gradle](http://www.gradle.org/) eingesetzt.

#Versionskontrolle mit Git

Die Basis für eine automatisierte Testumgebung liefert ein Versionskontrollsystem. Bei diesem Testprojekt fiel die Wahl auf [Git](http://git-scm.com/), da es sich sehr gut mit den weiteren Softwarequalitätstools verbinden lässt.
Grundsätzlich hat man für die Einrichtung einer automatisierten Testumgebung auf Basis von Git zwei Möglichkeiten:

1. Man nutzt einen Service (z.B. GitHub), über den man das Git-Repository bezieht.
1. Das Repository wird selbst gehostet. Man richtet einen Post-Receive Hook ein.

##Methode 1: Nutzung von Github

Zunächst muss ein Repository bei Github erstellt werden und alle beteiligten Entwickler als Collaborators eingetragen werden. Anschließend können alle Entwickler das Repository auf ihrem Entwicklerrechner klonen:
    
    $ git clone https://github.com/test/testprojekt.git <- richtige url

##Methode 2: Einsatz eines eigenen Git Servers

Bei dieser Methode hostet man einen eigenen Git Server. Da git die Möglichkeit des [Einrichtens von Hooks](http://git-scm.com/docs/githooks) mitbringt, lässt sich so relativ einfach eine Automatisierung nach jedem Push auf den Server erreichen. Die benötigten Schritte zur Einrichtung eines git Servers und Anlegen eines Hooks zur Automatisierung sind im Folgenden beschrieben.

###Git-Repository erstellen

Um ein git-Repository zu erstellen, müssen folgenden Schritte ausgeführt werden:

1. Sich mit dem Server über SSH verbinden: `$ ssh root@serverip`
1. git auf dem Server installieren: `$ apt-get install git`
1. Neuen User “git” anlegen: `$ adduser git`
1. Ordner für Repositories anlegen (Das /home/git Verzeichnis des git-Users dient als Ablage für die git-Repositories): `$ mkdir /home/git`
1. Zum Ordner wechseln: `$ cd /home/git`
1. Das Repository erstellen: `$ git init --bare testprojekt`

Zusammengefasst die Schritte 1-6:

    $ ssh root@serverip
    $ apt-get install git
    $ adduser git
    $ mkdir /home/git
    $ cd /home/git
    $ git init --bare testprojekt

###SSH-Zugriff konfigurieren
Der Zugriff auf das neu erstellte Repository erfolgt mittels SSH. Dafür wird den git-User SSH konfiguriert:

    $ ssh git@serverip
    $ mkdir .ssh
    
Damit nun jeder Entwickler Zugriff auf das neu erstellte Repository erhält, muss anschließend jeder Entwickler ein private/public-Schlüsselpaar erstellen:

    $ ssh-keygen -t dsa

Wurde der standardmäßige Dateiname gewählt, werden zwei Schlüssel generiert:

* `id_dsa` , der Private-Key, geschützt durch das gewählte Passwort
* `id_dsa.pub` , der Public-Key, welcher auf den Server übertragen werden muss

Alle Entwickler, die an den Projekten mitarbeiten sollen, müssen ihren Public-Key bereitstellen.
Die Public-Keys aller Entwickler müssen daraufhin auf den Server übertragen werden.
Dies lässt sich vom Entwicklerrechner aus folgendermaßen bewerkstelligen:

    $ scp id_dsa.pub git@serverip:/.ssh/entwickler_name.pub

Falls der key in einem anderen Format als OpenSSH ist, muss er konvertiert werden:

    $ ssh-keygen -i -f entwickler_name.pub  > entwickler_name.com.pub

Anschließend muss der Key eingetragen werden:

    $ cat entwickler_name.com.pub >> .ssh/authorized_keys
    
Im Anschluss muss nun der Entwickler, der das Projekt anlegt, einen intialen Commit erstellen und den Server festlegen. Dies lässt sich mit folgenden Kommandos durchführen:

    $ git add .gitignore
    $ git commit -m’.gitignore added’
    $ git add --all :
    $ git commit -m’initial commit’
    $ git remote add origin git@serverip:testprojekt
    $ git push origin master
    
Nun können alle weiteren Entwickler das erzeugte Projekt klonen:

    $ git clone git@serverip:testprojekt
    
##Komponententests

Jeder Entwickler möchte nach dem Pushen auf den Server möglichst schnell (nicht mehr als 10 Minuten) sehen, ob alle vorhandenen Tests (noch) durchlaufen. Dauert das Durchlaufen der Tests zu lange, besteht die Gefahr, dass das Testsystem als Hürde angesehen wird und auf das Schreiben von Tests verzichtet wird.
Um den Entwicklern ein schnelles Feedback zu geben wurde in der Testautomatisierung darauf verzichtet, ein virtuelles Gerät zu starten, da dies zu lange dauern würde. Stattdessen wurde die Library [Robolectric](http://robolectric.org/) benutzt. Damit wird es möglich, Android-Code in der Java Virtual Machine (JVM) auszuführen und so innerhalb kurzer Zeit das Ergebnis zu erhalten. 

##Integrationstests

Für die Integrationstest kam [Robotium](https://code.google.com/p/robotium/) zum Einsatz. Mit Robotium ist es möglich, Black-Box Tests über mehrere Activities hinweg zum erstellen, die über das User Interface Funktionalitäten prüfen. Dies steht dem manuellen Testen von Anwendungen in nichts nach; zusätzlich besteht der große Vorteil, dass die angelegten Tests automatisiert durchlaufen und jederzeit wiederholt werden können. Somit kann die korrekte Funktionsweise der App aus Nutzersicht überprüft werden.
Robotium bietet eine einfach zu erlernende Api zum Erstellen der Testfälle. Darüber wird es beispielsweise möglich, Textfelder auszufüllen, Buttons zu drücken oder auf das Öffnen von anderen Activities zu warten.



Robotium wurde in die Testumgebung integriert. Anders als bei den Komponententests werden die Integrationstests jedoch nicht bei jedem Push auf den Server automatisch ausgeführt, sondern können mit diesem Kommando über Gradle gestartet werden:

    $ ./gradlew robotium
    
##Diskussion

In diesem Testprojekt wurde eine Lösung geschaffen, mit der man die Qualität eines Android-Projekts deutlich steigern kann. Dies wurde in erster Linie durch das Bereitstellen einer visuellen Dashboards (Sonarqube) erreicht, das zu jeder Zeit den aktuellen Stand des Softwareprodukts widerspiegelt. Neben statischen Analysen liefert dieses auch Informationen zu den durchgelaufenen Tests und zur Testabdeckung. Dieses Setup bietet einen deutlichen Mehrwert, da es neben den Informationen auch jederzeit eine Motivation für die Entwickler bietet: Es liefert einen Anreiz, qualitativ hochwertigeren Code mit besserer Codeabdeckung, weniger Komplexität und mehr Dokumentation zu schreiben, da die Ergebnisse auf dem Dashboard sofort sichtbar sind und man auch die Entwicklung zu vorherigen Softwareständen begutachten kann. 
Das Einrichten dieses Systems war jedoch mit sehr vielen Schwierigkeiten verbunden. Dies hatte mehrere Gründe. Zum einen gibt es im Themengebiet Android-Testautomation relativ wenige Dokumentation – anders als man es in Hinblick auf dieses wichtige Thema vermuten würde. Gerade, wenn man eine gesamte automatisierte Testumgebung aufbauen möchte, gibt es zwar zu den einzelnen Tools/Werkzeuge nützliche Informationen und Dokumentationen, jedoch fehlt oft die Beschreibung, wie man diese mit anderen Tools kombiniert bzw. in andere Tools integriert. Um das reibenungslose Zusammenspiel von Git, Jenkins, Sonarqube, Sonarqube-Runner, Gradle, Maven, Jacoco, Robolectic, Robotium zu erreichen, musste im Laufe der Einrichtung der Testumgebung oftmals die Strategie gewechselt und das System auf eine andere Weise eingerichtet werden. Folgende Möglichkeiten wurden im Laufe des Projekts ausprobiert und durch andere Methoden ersetzt:

* Unabhängige Projekte für den Entwicklungs- und Testcode. Verwaltung der Projekte mit Maven.
Statt Maven wurde Gradle eingesetzt. Dadurch lassen sich Entwicklungs- und Testcode in einem Projekt verwalten.
* Testen auf dem Server mit virtuellem Device. Umstellung auf Robolectric.
Durch Robolectric braucht man keinerlei Device mehr, wodurch die Tests deutlich schneller ausgeführt werden.
* Integrationstest mit JUnit. Umstellung auf Robotium.
Robotium liefert die Möglichkeit, auf UI-Ebene zu testen.
* Beziehen eines Git-Repositorys über GitHub. Umstellung auf eigenes Git-Repository durch Erstellen eines Git-Server. Sonarqube lässt sich so einfacher ansprechen.
* Einsatz von Jenkins als CI-Server. Umstellung auf Travis CI, da dies eine Integration mit GitHub bietet. Zuletzt wurde auf CI-Server verzichtet, da dieser mit eigenem Git-Server nicht mehr notwendig war.

Aufgrund dieser vielen Anpassung entstand enormer Aufwand. Jedoch konnte durch diesen Prozess einiges an Wissen erlangt werden.

##Fazit

Beim Thema Testautomatisierung und Continuous Integration gibt es für fast alle Programmiersprachen eine Vielzahl an verschiedenen Tools, Libraries und Lösungen, mit denen man für Softwareprojekte ein System erstellen kann, mit dem man die Softwarequalität objektiv messen kann und das die Entwickler dazu motiviert, “besseren” und getesten Code zu schreiben. Doch gerade in der Vielzahl der Tools liegt die größte Schwierigkeit: Es gibt nicht die beste Testumgebung. In dieser Arbeit wurde ein System für die testgetriebene Java/Android-Entwicklung vorgestellt, das statische Codeanalysen automatisiert durchführt und alle notwendigen Features für eine testgetriebene Entwicklung mitbringt (Testausführung, Messung der Testabdeckung, Möglichkeit von Integrationstest). Zwar gab es auf dem Weg zum zufriedenstellenden System einige Stolpersteine, jedoch hat sich der Aufwand gelohnt, da nun ein Gesamtsystem entstanden ist, das sich für weitere Android-Projekte einsetzen lässt.

Doch auch eine sehr gute automatisierte Testumgebung bringt in der Praxis nichts, wenn die Entwickler sich nicht mit der testgetriebenen Entwicklung anfreunden können und gar nicht oder nur selten Testfälle schreiben. In diesem Testprojekt waren die Entwickler TDD zwar positiv gegenüber eingestimmt, jedoch war es für alle das erste Projekt, bei dem man komplett testgetrieben entwickeln sollte. Aus diesem Grund war die Testabdeckung relativ gering – was zum Ende des Projekts auch dem zunehmendem Abgabestress geschuldet war. Doch alle Teilnehmer haben wertvolle Erfahrungen sammeln können und würden das nächste Mal wieder testgetrieben entwickeln wollen, denn auch “eine nicht vollständige Testabdeckung ist besser als gar keine Tests”, so das Resume der Teilnehmer. Mit dieser Arbeit wurde ein System geschaffen, das als Basis und Anleitung für weitere testgetriebene Android-Projekte verwendet werden kann.
