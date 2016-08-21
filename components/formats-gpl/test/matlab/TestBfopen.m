% Integration tests for the bfopen utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

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

classdef TestBfopen < ReaderTest
    
    properties
        filepath
        data
        nSeries = 1
    end
    
    methods
        function self = TestBfopen(name)
            self = self@ReaderTest(name);
        end
        
        function tearDown(self)
            if exist(self.filepath,'file') == 2, delete(self.filepath); end
            self.data = [];
            tearDown@ReaderTest(self);
        end
        
        function checkFake(self, fakefilename)
            % Create fake file
            if isunix,
                self.filepath = fullfile('/tmp', fakefilename);
            else
                self.filepath = fullfile('C:', fakefilename);
            end
            fid = fopen(self.filepath, 'w+');
            fclose(fid);
            
            % Read fake file using bfopen
            self.data = bfopen(self.filepath);
            
            % Test dimensions of bfopen output and core metadata
            nPlanes = self.sizeZ * self.sizeC * self.sizeT;
            assertEqual(size(self.data), [self.nSeries 4]);
            for i = 1 : self.nSeries
                assertEqual(size(self.data{i, 1}), [nPlanes 2]);
                m = self.data{i,4};
                assertEqual(m.getImageCount(), self.nSeries);
                assertEqual(m.getPixelsSizeZ(i-1).getValue(), self.sizeZ);
                assertEqual(m.getPixelsSizeC(i-1).getValue(), self.sizeC);
                assertEqual(m.getPixelsSizeT(i-1).getValue(), self.sizeT);
            end
        end
        
        function testDefault(self)
            self.checkFake('test.fake')
        end
        
        function testMultiSeries(self)
            self.nSeries = 3;
            self.checkFake(['test&series=' num2str(self.nSeries) '.fake'])
        end
        
        function testSizeZ(self)
            self.sizeZ = 3;
            self.checkFake(['test&sizeZ=' num2str(self.sizeZ) '.fake'])
        end
        
        function testSizeC(self)
            self.sizeC = 3;
            self.checkFake(['test&sizeC=' num2str(self.sizeC) '.fake'])
        end
        
        function testSizeT(self)
            self.sizeT = 3;
            self.checkFake(['test&sizeT=' num2str(self.sizeT) '.fake'])
        end
    end
end
