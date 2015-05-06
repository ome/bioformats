% Tests for the bfGetReader utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2013-2014 Open Microscopy Environment:
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

classdef TestBfGetReader < ReaderTest
    
    methods
        function self = TestBfGetReader(name)
            self = self@ReaderTest(name);
        end
        
        % Input check tests
        function testInputClass(self)
            assertExceptionThrown(@() bfGetReader(0),...
                'MATLAB:InputParser:ArgumentFailedValidation');
        end
        
        function testNoInput(self)
            self.reader = bfGetReader();
            assertTrue(isa(self.reader, 'loci.formats.ReaderWrapper'));
            assertTrue(isempty(self.reader.getCurrentFile()));
        end
        
        function testEmptyInput(self)
            self.reader = bfGetReader('');
            assertTrue(isa(self.reader, 'loci.formats.ReaderWrapper'));
            assertTrue(isempty(self.reader.getCurrentFile()));
        end
        
        function testFakeInput(self)
            self.reader = bfGetReader('test.fake');
            assertTrue(isa(self.reader, 'loci.formats.ReaderWrapper'));
            assertEqual(char(self.reader.getCurrentFile()), 'test.fake');
        end
        
        function testNonExistingInput(self)
            assertExceptionThrown(@() bfGetReader('nonexistingfile'),...
                'bfGetReader:FileNotFound');
        end
        
        function testFormatTypeInput(self)
            self.reader = bfGetReader('test.fake');
            assertEqual(char(self.reader.getFormat),'Simulated data');
        end
        
        function testFileInput(self)
            % Create fake file
            mkdir(self.tmpdir);
            filepath = fullfile(self.tmpdir, 'test.fake');
            fid = fopen(filepath, 'w+');
            fclose(fid);
            self.reader = bfGetReader(filepath);
            rmdir(self.tmpdir, 's');
            assertTrue(isa(self.reader, 'loci.formats.ReaderWrapper'));
            assertEqual(char(self.reader.getCurrentFile()), filepath);
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
        
        function testGetPixelsPhysicalSizeX(self)
            self.reader = bfGetReader('pixelSize-test&physicalSizeX=.3.fake');
            metadata = self.reader.getMetadataStore();
            physicalSizeX = metadata.getPixelsPhysicalSizeX(0);
            assertFalse(isempty(physicalSizeX));
            assertEqual(physicalSizeX.value().doubleValue(), .3);
            assertEqual(char(physicalSizeX.unit().getSymbol()), 'µm');
            assertElementsAlmostEqual(physicalSizeX.value(ome.units.UNITS.NM).doubleValue(), 300.0);
        end
        
        function testGetPixelsPhysicalSizeY(self)
            self.reader = bfGetReader('pixelSize-test&physicalSizeY=.3.fake');
            metadata = self.reader.getMetadataStore();
            physicalSizeY = metadata.getPixelsPhysicalSizeY(0);
            assertFalse(isempty(physicalSizeY));
            assertEqual(physicalSizeY.value().doubleValue(), .3);
            assertEqual(char(physicalSizeY.unit().getSymbol()), 'µm');
            assertElementsAlmostEqual(physicalSizeY.value(ome.units.UNITS.NM).doubleValue(), 300.0);
        end
        
        function testGetPixelsPhysicalSizeZ(self)
            self.reader = bfGetReader('pixelSize-test&physicalSizeZ=.3.fake');
            metadata = self.reader.getMetadataStore();
            physicalSizeZ = metadata.getPixelsPhysicalSizeZ(0);
            assertFalse(isempty(physicalSizeZ));
            assertEqual(physicalSizeZ.value().doubleValue(), .3);
            assertEqual(char(physicalSizeZ.unit().getSymbol()), 'µm');
            assertElementsAlmostEqual(physicalSizeZ.value(ome.units.UNITS.NM).doubleValue(), 300.0);
        end
        
        function testJavaMethod(self)
            self.reader = loci.formats.ChannelFiller();
            self.reader = loci.formats.ChannelSeparator(self.reader);
            
            reader = javaObject('loci.formats.ChannelSeparator', ...
                javaObject('loci.formats.ChannelFiller'));
            assertEqual(self.reader.getClass(),reader.getClass());
            
            OMEXMLService = loci.formats.services.OMEXMLServiceImpl();
            OMEXMLService1 = javaObject('loci.formats.services.OMEXMLServiceImpl');
            assertEqual(OMEXMLService.getClass(),OMEXMLService1.getClass());
            
            self.reader.setMetadataStore(OMEXMLService1.createOMEXMLMetadata());
        end
        
        %Test Default Thumb Size
        function testThumbSizeX(self)
            self.reader = bfGetReader('test.fake');
            assertEqual(self.reader.getThumbSizeX(),128);
        end
        
        function testThumbSizeY(self)
            self.reader = bfGetReader('test.fake');
            assertEqual(self.reader.getThumbSizeY(),128);
        end
       
    end
end