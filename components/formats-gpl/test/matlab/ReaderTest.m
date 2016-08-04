% Abstract class for the Bio-Formats Matlab unit tests using readers
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2014 - 2015 Open Microscopy Environment:
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

classdef ReaderTest < TestBfMatlab
    
    properties
        reader
        sizeX
        sizeY
        sizeZ
        sizeC
        sizeT
    end
    
    methods
        function self = ReaderTest(name)
            self = self@TestBfMatlab(name);
        end
        
        function setUp(self)
            setUp@TestBfMatlab(self)
            javaaddpath(self.jarPath);
            self.reader = loci.formats.in.FakeReader();
            self.sizeX = self.reader.DEFAULT_SIZE_X;
            self.sizeY = self.reader.DEFAULT_SIZE_Y;
            self.sizeZ = self.reader.DEFAULT_SIZE_Z;
            self.sizeC = self.reader.DEFAULT_SIZE_C;
            self.sizeT = self.reader.DEFAULT_SIZE_T;
            loci.common.DebugTools.enableLogging('ERROR');
            import ome.units.UNITS.*;
        end
        
        function tearDown(self)
            if ~isempty(self.reader),
                self.reader.close();
                self.reader = [];
            end
            tearDown@TestBfMatlab(self)
        end
    end
end
