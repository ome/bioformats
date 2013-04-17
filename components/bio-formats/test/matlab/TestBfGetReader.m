% Tests for the bfGetReader utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2013 Open Microscopy Environment:
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

classdef TestBfGetReader < TestCase
    
    properties
        reader
    end
    
    methods
        function self = TestBfGetReader(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            bfCheckJavaPath();
        end
        
        function tearDown(self)
            self.reader.close();
            self.reader = [];
        end
        
        % Pixel type tests
        function checkPixelsType(self, type)
            self.reader = bfGetReader([type '-test&pixelType=' type '.fake']);
            pixelType = self.reader.getPixelType();
            class = char(loci.formats.FormatTools.getPixelTypeString(pixelType));
            assertEqual(class, type);
        end
        
        function testINT8(self)
            self.checkPixelsType('int8');
        end
        function testUINT8(self)
            self.checkPixelsType('uint8');
        end
        function testINT16(self)
            self.checkPixelsType('int16');
        end
        function testUINT16(self)
            self.checkPixelsType('uint16');
        end
        function testINT32(self)
            self.checkPixelsType('int32');
        end
        function testUINT32(self)
            self.checkPixelsType('uint32');
        end
        function testSINGLE(self)
            self.checkPixelsType('float');
        end
        function testDOUBLE(self)
            self.checkPixelsType('double');
        end
        
        % Dimension size tests
        function testSizeX(self)
            sizeX = 200;
            self.reader = bfGetReader(['sizeX-test&sizeX=' num2str(sizeX) '.fake']);
            assertEqual(self.reader.getSizeX(), sizeX);
        end
        
        function testSizeY(self)
            sizeY = 200;
            self.reader = bfGetReader(['sizeY-test&sizeY=' num2str(sizeY) '.fake']);
            assertEqual(self.reader.getSizeY(), sizeY);
        end
        
        function testSizeZ(self)
            sizeZ = 200;
            self.reader = bfGetReader(['sizeZ-test&sizeZ=' num2str(sizeZ) '.fake']);
            assertEqual(self.reader.getSizeZ(), sizeZ);
        end
        
        function testSizeC(self)
            sizeC = 200;
            self.reader = bfGetReader(['sizeC-test&sizeC=' num2str(sizeC) '.fake']);
            assertEqual(self.reader.getSizeC(), sizeC);
        end
        
        function testSizeT(self)
            sizeT = 200;
            self.reader = bfGetReader(['sizeT-test&sizeT=' num2str(sizeT) '.fake']);
            assertEqual(self.reader.getSizeT(), sizeT);
        end
        
        % Endianness tests
        function testLittleEndian(self)
            self.reader = bfGetReader('little-test&little=true.fake');
            assertTrue(self.reader.isLittleEndian());
        end
        
        function testBigEndian(self)
            self.reader = bfGetReader('bigendian-test&little=false.fake');
            assertFalse(self.reader.isLittleEndian());
        end
        
        % Series tests
        function testMonoSeries(self)
            nSeries = 1;
            self.reader = bfGetReader(['series-test&series=' num2str(nSeries) '.fake']);
            assertEqual(self.reader.getSeriesCount(), nSeries);
        end
        
        function testMultiSeries(self)
            nSeries = 10;
            self.reader = bfGetReader(['series-test&series=' num2str(nSeries) '.fake']);
            assertEqual(self.reader.getSeriesCount(), nSeries);
        end
        
        function testInterleaved(self)
            self.reader = bfGetReader('interleaved-test&interleaved=true.fake');
            assertTrue(self.reader.isInterleaved());
        end
        
        function testNoInterleaved(self)
            self.reader = bfGetReader('interleaved-test&interleaved=false.fake');
            assertFalse(self.reader.isInterleaved());
        end
    end
end
