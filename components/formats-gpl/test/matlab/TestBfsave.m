% Integration tests for the bfSave utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2012 - 2016 Open Microscopy Environment:
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

classdef TestBfsave < ReaderTest
    
    properties
        path
        I
        dimensionOrder = 'XYZCT'
    end
    
    methods
        function self = TestBfsave(name)
            self = self@ReaderTest(name);
        end
        
        function setUp(self)
            setUp@ReaderTest(self);
            mkdir(self.tmpdir);
            self.path = fullfile(self.tmpdir, 'test.ome.tif');
        end
        
        function tearDown(self)
            if exist(self.tmpdir, 'dir') == 7, rmdir(self.tmpdir, 's'); end
            tearDown@ReaderTest(self);
        end
        
        function checkMinimalMetadata(self)
            % Check dimensions of saved ome-tiff
            self.reader = bfGetReader(self.path);
            d = size(self.I);
            assertEqual(self.reader.getSizeX(), d(2));
            assertEqual(self.reader.getSizeY(), d(1));
            assertEqual(self.reader.getSizeZ(), d(self.dimensionOrder=='Z'));
            assertEqual(self.reader.getSizeC(), d(self.dimensionOrder=='C'));
            assertEqual(self.reader.getSizeT(), d(self.dimensionOrder=='T'));
            assertEqual(char(self.reader.getDimensionOrder()), self.dimensionOrder);
            metadataStore = self.reader.getMetadataStore();
            assertEqual(char(metadataStore.getImageID(0)), 'Image:0');
            assertEqual(char(metadataStore.getPixelsID(0)), 'Pixels:0');
            for i = 1 : d(self.dimensionOrder=='C')
                assertEqual(char(metadataStore.getChannelID(0, i - 1)),...
                    sprintf('Channel:0:%g', i - 1));
            end

        end
        
        % Input check tests
        function testNoInput(self)
            assertExceptionThrown(@() bfsave(),...
                'MATLAB:InputParser:notEnoughInputs');
        end
        
        function testNoOutputPath(self)
            self.I = 1;
            assertExceptionThrown(@() bfsave(self.I),...
                'MATLAB:InputParser:notEnoughInputs');
        end
        
        function testInvalidI(self)
            self.I = 'a';
            assertExceptionThrown(@() bfsave(self.I, self.path),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testInvalidDimensionOrder(self)
            self.I = 1;
            assertExceptionThrown(@() bfsave(self.I, self.path, 'XY'),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        % Dimension order tests
        function testDimensionOrderXYZCT(self)
            self.dimensionOrder = 'XYZCT';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        function testDimensionOrderXYZTC(self)
            self.dimensionOrder = 'XYZTC';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        function testDimensionOrderXYCZT(self)
            self.dimensionOrder = 'XYCZT';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        function testDimensionOrderXYCTZ(self)
            self.dimensionOrder = 'XYCTZ';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        function testDimensionOrderXYTCZ(self)
            self.dimensionOrder = 'XYTCZ';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        function testDimensionOrderXYTZC(self)
            self.dimensionOrder = 'XYTZC';
            self.I = zeros(2, 3, 4, 5, 6);
            bfsave(self.I, self.path, self.dimensionOrder);
            self.checkMinimalMetadata();
        end
        
        % Pixel type tests
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
            self.reader = bfGetReader('plane.fake');
            self.I = bfGetPlane(self.reader, 1);
            bfsave(self.I, self.path);
            assertEqual(imread(self.path), self.I);
        end
        
        % Compression type tests
        function checkCompression(self, type, nonlossy)
            self.reader = bfGetReader('plane.fake');
            self.I = bfGetPlane(self.reader, 1);
            bfsave(self.I, self.path, 'Compression', type);
            if nonlossy
                assertEqual(imread(self.path), self.I);
            end
        end
        
        function testJPEG(self)
            self.checkCompression('JPEG', false);
        end
        
        function testJPEG2000(self)
            self.checkCompression('JPEG-2000', false);
        end
        
        function testJPEG2000Lossy(self)
            self.checkCompression('JPEG-2000 Lossy', false);
        end
        
        function testUncompressed(self)
            self.checkCompression('Uncompressed', true);
        end
        
        function testLZW(self)
            self.checkCompression('LZW', true);
        end
        
        % Big-tiff test
        function testBigTiff(self)
            self.I = zeros(100, 100);
            bfsave(self.I, self.path, 'BigTiff', true);
            assertEqual(imread(self.path), self.I);
        end
        
        % Metadata test
        function testCreateMinimalOMEXMLMetadata(self)
            self.I = zeros(2, 3, 4, 5, 6);
            metadata = createMinimalOMEXMLMetadata(self.I);
            bfsave(self.I, self.path, 'metadata', metadata);
            self.checkMinimalMetadata();
        end
        
        function testAdditionalOMEXMLMetadata(self)
            self.I = zeros(2, 3, 4, 5, 6);
            metadata = createMinimalOMEXMLMetadata(self.I);
            metadata.setImageDescription('description',0);
            bfsave(self.I, self.path, 'metadata', metadata);
            self.checkMinimalMetadata();
            d = self.reader.getMetadataStore().getImageDescription(0);
            assertEqual(char(d), 'description');
        end
    end
end
