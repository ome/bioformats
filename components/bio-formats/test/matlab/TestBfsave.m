% Integration tests for the bfSave utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2013 Open Microscopy Environment:
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

classdef TestBfsave < TestCase
    
    properties
        path
        I
    end
    
    methods
        function self = TestBfsave(name)
            self = self@TestCase(name);
        end
        
        function setUp(self)
            bfCheckJavaPath();
            if isunix,
                self.path = '/tmp/test.ome.tiff';
            else
                self.path = 'C:\test.ome.tiff';
            end
        end
        
        function tearDown(self)
            if exist(self.path,'file')==2, delete(self.path); end
        end
        
        % Dimension order tests
        function checkDimensionOrder(self, dimensionOrder)
            self.I = zeros(2, 3, 4, 5, 6);
            d = size(self.I);
            bfsave(self.I, self.path, dimensionOrder);
            
            % Check dimensions of saved ome-tiff
            reader = bfGetReader(self.path);
            assertEqual(reader.getSizeX, d(2));
            assertEqual(reader.getSizeY, d(1));
            assertEqual(reader.getSizeZ, d(dimensionOrder=='Z'));
            assertEqual(reader.getSizeC, d(dimensionOrder=='C'));
            assertEqual(reader.getSizeT, d(dimensionOrder=='T'));
            reader.close();
        end
        
        function testDimensionOrderXYZCT(self)
            self.checkDimensionOrder('XYZCT')
        end
        
        function testDimensionOrderXYZTC(self)
            self.checkDimensionOrder('XYZTC')
        end
        
        function testDimensionOrderXYCZT(self)
            self.checkDimensionOrder('XYCZT')
        end
        
        function testDimensionOrderXYCTZ(self)
            self.checkDimensionOrder('XYCTZ')
        end
        
        function testDimensionOrderXYTCZ(self)
            self.checkDimensionOrder('XYTCZ')
        end
        
        function testDimensionOrderXYTZC(self)
            self.checkDimensionOrder('XYTZC')
        end
        
        % Data type tests
        function checkPixelType(self, type)
            self.I = zeros(100, 100, type);
            bfsave(self.I, self.path);
            assertEqual(imread(self.path), self.I);
        end
        
        function testPixelsTypeUINT8(self)
            self.checkPixelType('uint8');
        end
        
        function testPixelsTypeINT8(self)
            self.checkPixelType('int8');
        end
        
        function testPixelsTypeUINT16(self)
            self.checkPixelType('uint16');
        end
        
        function testPixelsTypeINT16(self)
            self.checkPixelType('int16');
        end
        
        function testPixelsTypeUINT32(self)
            self.checkPixelType('uint32');
        end
        
        function testPixelsTypeINT32(self)
            self.checkPixelType('int32');
        end
        
        function testPixelsTypeFLOAT(self)
            self.checkPixelType('single');
        end
        
        function testPixelsTypeDOUBLE(self)
            self.checkPixelType('double');
        end
        
        % Bytes reading tests
        function testSinglePoint(self)
            self.I = 1;
            bfsave(self.I, self.path);
            assertEqual(imread(self.path), self.I);
        end
        
        function testSinglePlane(self)
            r = bfGetReader('plane.fake');
            self.I = bfGetPlane(r, 1);
            bfsave(self.I, self.path);
            assertEqual(imread(self.path), self.I);
        end
    end
end
