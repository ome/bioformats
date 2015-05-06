% Integration tests for the bfGetFileExtensions utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2015 Open Microscopy Environment:
%   - Board of Regents of the University of Wisconsin-Madison
%   - Glencoe Software, Inc.
%   - University of Dundee
%
% This program is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as
% published by the Free Software Foundation, either version 2 of the
% License, or (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along
% with this program; if not, write to the Free Software Foundation, Inc.,
% 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

classdef TestBfGetFileExtensions < TestBfMatlab
    
    properties
        readers
    end
    
    methods
        
        function self = TestBfGetFileExtensions(name)
            self = self@TestBfMatlab(name);
        end
        
        function testOctaveComaptibility(self)
            
            self.readers = javaMethod('getReaders', javaObject('loci.formats.ImageReader'));
            
            fileExt = cell(numel(self.readers), 2);
            for i = 1:numel(self.readers)
                suffixes = self.readers(i).getSuffixes();
                fileExt{i, 1} = arrayfun(@char, suffixes, 'Unif', false);
                
                ExtSuf = cell(numel(suffixes), 1);
                for j = 1:numel(suffixes)
                    ExtSuf{j} = char(suffixes(j));
                end
                fileExt{i, 1} = ExtSuf;%Octave Implementation
                fileExt{i, 2} = arrayfun(@char, suffixes, 'Unif', false);%Current Matlab implementation
                assertEqual(fileExt{i,1},fileExt{i,2});
            end
            
        end
        
        
    end
    
end