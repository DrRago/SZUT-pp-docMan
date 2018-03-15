# Aufgabenbeschreibung
Erstellt werden soll ein Programm zur Verwaltung von Dokumenten für eine Firma. Die Daten sollen aus einer SQL Datenbank ausgelesen und dort abgelegt werden. Das Programm soll über ein entsprechende GUI die Anzeige, Veränderung und das Anlegen und Löschen von Einträgen unterstützen.

## Datenstruktur
Zu jedem Dokument soll eine eindeutige Identifikationsbezeichnung, die vom Nutzer festgelegt werden kann, abgelegt werden. Außerdem soll für ein Dokument ein Titel, ein Autor, eine Menge von Schlagworten und ein Fundort (Referenz) abgelegt werden. Je nach Beschaffenheit des Dokuments ist der Fundort entweder eine Ablagebezeichnung im Archiv (bestehend aus Bereich (shed), Regal (rack) und Ordner (folder)) oder eine Datei (bestehend aus einem Dateipfad und Dateinamen, der in der Applikation ausgewählt werden kann), oder eine URL.

## Entwicklungsoptionen
Das Projekt soll in Gruppen zu zwei oder drei Personen bearbeitet werden. Dreiergruppen sollen zusätzlich eine Suchfunktion implementieren, mit der ein Dokument anhand der ID, des Titel, des Autors, oder Schlagwörter oder einer Kombination aus einigen dieser Angaben (AND-Verknüpfung) aufgefunden werden kann.

Die Anbindung der Datenbank kann manuell programmiert oder über einen [OR-Mapper](https://de.wikipedia.org/wiki/OR_Mapper) ( [SQLObject](http://www.sqlobject.org), [Hibernate](http://hibernate.org/orm), o. ä.) erfolgen. Die Datenbankanbindung soll von der GUI durch entsprechende Kapselung separiert werden ( [Model-View-Controller](https://de.wikipedia.org/wiki/Model_View_Controller) o. ä. mit entsprechenden Klassen für das Datenmodell; die Abstraktion über [DAO](https://de.wikipedia.org/wiki/Data_Access_Object)s ist nicht notwendig). Als Datenbank soll mindestens entwerder [SQlite](https://www.sqlite.org) oder [HyperSQL Embedded](http://hsqldb.org) unterstützt werden.

Zusätzliche Abhängigkeiten zu Bibliotheken, die im Schulnetz nicht vorhanden sind, müssen in das Projekt eingebettet oder über Maven bzw. Pythons [setuptools](http://python-packaging.readthedocs.io/en/latest/dependencies.html) referenziert werden.

## Allgemeines
Das Programm soll unter Berücksichtigung der Grundlagen der objektorientierten Programmierung entworfen und entwickelt werden. Die Implementierung soll den bekannten Standards und Qualitätskriterien folgen und hinreichend dokumentiert und getestet werden. Außerdem sollen Fehler abgefangen und entsprechend berücksichtigt werden.

Dazu soll ein entsprechendes Protokoll nach den vorliegenden Vorgaben erstellt werden. Im Protokoll soll mindestens eine Klassenübersicht als UML Klassendiagramm enthalten sein. Die Softwaretests sollen hinreichend beschrieben und das Ergebnis jeweils protokolliert werden.