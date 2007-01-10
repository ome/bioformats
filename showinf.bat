@echo off

rem LOCI Bio-Formats package for reading and converting biological file formats.
rem Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
rem and Eric Kjellman.
rem 
rem This program is free software; you can redistribute it and/or modify
rem it under the terms of the GNU General Public License as published by
rem the Free Software Foundation; either version 2 of the License, or
rem (at your option) any later version.
rem 
rem This program is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU General Public License for more details.
rem 
rem You should have received a copy of the GNU General Public License
rem along with this program; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

rem showinf: a script for displaying information about a given image file,
rem          while displaying it in the image viewer

java -mx512m loci.formats.ImageReader %1 %2 %3 %4 %5 %6 %7 %8 %9
