@echo off

rem LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
rem Data Browser, OME Plugin and Bio-Formats Exporter.
rem Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
rem Curtis Rueden, Philip Huettl and Francis Wong.
rem  
rem This program is free software; you can redistribute it and/or modify it
rem under the terms of the GNU Library General Public License as published by
rem the Free Software Foundation; either version 2 of the License, or
rem (at your option) any later version.
rem  
rem This program is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU Library General Public License for more details.
rem  
rem You should have received a copy of the GNU Library General Public License
rem along with this program; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

rem showij: a script for displaying a given image file in ImageJ
rem         using the Bio-Formats Importer plugin

java -mx512m loci.plugins.Importer %1 %2 %3 %4 %5 %6 %7 %8 %9
