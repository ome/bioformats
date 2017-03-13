% Integration tests for the Memoizer reader
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2014 - 2017 Open Microscopy Environment:
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

classdef TestMemoizer < ReaderTest

    properties
        filepath
    end

    methods
        function self = TestMemoizer(name)
            self = self@ReaderTest(name);
        end

        function setUp(self)
            setUp@ReaderTest(self);

            % Create fake file for testing
            mkdir(self.tmpdir);
            self.filepath = fullfile(self.tmpdir, 'test.fake');
            fid = fopen(self.filepath, 'w+');
            fclose(fid);
        end
        function tearDown(self)
            if exist(self.tmpdir, 'dir') == 7, rmdir(self.tmpdir, 's'); end
            tearDown@ReaderTest(self);
        end

        function testMinimumElapsed(self)
            % Create memoizer reader with large minimum initialization
            % time
            self.reader = loci.formats.Memoizer(bfGetReader(), 1e10);
            self.reader.setId(self.filepath);

            % Check reader is not saved to memo file
            assertFalse(self.reader.isLoadedFromMemo());
            assertFalse(self.reader.isSavedToMemo());
            self.reader.close();
        end

        function testInPlaceCaching(self)

            % Create memoizer reader and initializes file
            self.reader = loci.formats.Memoizer(bfGetReader(), 0);
            self.reader.setId(self.filepath);

            % Check reader has been saved to memo file
            assertFalse(self.reader.isLoadedFromMemo());
            assertTrue(self.reader.isSavedToMemo());
            self.reader.close();
        end

        function testReinitialization(self)
            % Create Memoizer reader
            self.reader = loci.formats.Memoizer(bfGetReader(), 0);
            self.reader.setId(self.filepath);

            % Re-initialize the existing reader
            self.reader.close();
            self.reader.setId(self.filepath);

            % Check reader has been loaded from memo file
            assertTrue(self.reader.isLoadedFromMemo());
            assertFalse(self.reader.isSavedToMemo());
        end

        function testNewReader(self)
            % Create memoizer reader
            self.reader = loci.formats.Memoizer(bfGetReader(), 0);
            self.reader.setId(self.filepath);

            % Construct new memoizer
            reader2 = loci.formats.Memoizer(bfGetReader(), 0);
            reader2.setId(self.filepath);
            assertTrue(reader2.isLoadedFromMemo());
            assertFalse(reader2.isSavedToMemo());
            reader2.close();
            self.reader.close()
            clear reader2
        end

        function testMultiThreaded(self)
            % Create memoized reader
            self.reader = loci.formats.Memoizer(bfGetReader(), 0);
            self.reader.setId(self.filepath);

            localpath = self.filepath;
            for i = 1 : 4
                r = loci.formats.Memoizer(bfGetReader(), 0);
                r.setId(localpath);
                assertTrue(r.isLoadedFromMemo());
                assertFalse(r.isSavedToMemo());
                r.close();
            end
        end
    end
end
