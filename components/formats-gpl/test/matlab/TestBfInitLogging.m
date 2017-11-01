% TestBfInitLogging define test cases for bfInitLogging utility function
%
% Require MATLAB xUnit Test Framework to be installed
% http://www.mathworks.com/matlabcentral/fileexchange/22846-matlab-xunit-test-framework
% https://github.com/psexton/matlab-xunit (GitHub source code)

% OME Bio-Formats package for reading and converting biological file formats.
%
% Copyright (C) 2016 - 2017 Open Microscopy Environment:
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


classdef TestBfInitLogging < TestBfMatlab

    properties
        root
    end

    methods (TestMethodSetup)
        function InitLogging(self)
            % WIP: Is this strictly necessary?
            javaaddpath(self.jarPath);
            import org.apache.log4j.Logger;
            self.root = Logger.getRootLogger();
        end
    end

    methods (Test)
        function disableLogging(self)
            self.root.removeAllAppenders();
            bfCheckJavaPath();
            self.assertFalse(loci.common.DebugTools.isEnabled());
        end

        function testDefault(self)
            self.disableLogging();
            bfInitLogging();
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'WARN');
            bfInitLogging('INFO');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'WARN');
        end

        function testALL(self)
            self.disableLogging();
            bfInitLogging('ALL');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'ALL');
        end

        function testERROR(self)
            self.disableLogging();
            bfInitLogging('ERROR');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'ERROR');
        end

        function testDEBUG(self)
            self.disableLogging();
            bfInitLogging('DEBUG');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'DEBUG');
        end

        function testINFO(self)
            self.disableLogging();
            bfInitLogging('INFO');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'INFO');
        end

        function testFATAL(self)
            self.disableLogging();
            bfInitLogging('FATAL');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'FATAL');
        end

        function testOFF(self)
            self.disableLogging();
            bfInitLogging('OFF');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'OFF');
        end

        function testTRACE(self)
            self.disableLogging();
            bfInitLogging('TRACE');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'TRACE');
        end

        function testWARN(self)
            self.disableLogging();
            bfInitLogging('WARN');
            self.assertTrue(loci.common.DebugTools.isEnabled());
            self.assertEqual(char(self.root.getLevel.toString()), 'WARN');
        end

        function testSetRootLevel(self)
            self.disableLogging();
            loci.common.DebugTools.enableLogging();
            self.assertTrue(loci.common.DebugTools.isEnabled());
            loci.common.DebugTools.setRootLevel('INFO');
            self.assertEqual(char(self.root.getLevel.toString()), 'INFO');
            loci.common.DebugTools.setRootLevel('DEBUG');
            self.assertEqual(char(self.root.getLevel.toString()), 'DEBUG');
        end
    end
end
