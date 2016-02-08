function bfInitLogging(varargin)
% BFINITLOGGING initializes Bio-Formats and its logging level
%
%   bfInitLogging() autoloads Bio-Formats is in the Java path and checks
%   whether log4j is initialized. If not, it initializes logging at the
%   WARN level.
%
%   bfInitLogging(level) sets the logging to the input level.
%
% Examples
%
%    bfInitLogging();
%    bfInitLogging('DEBUG');
%
% Sebastien Besson, Nov 2014

% Autoload Bio-Formats Java path
bfCheckJavaPath();

% Return if log4j is alreadyinitialied
import org.apache.log4j.Logger;
root = Logger.getRootLogger();
appenders = root.getAllAppenders();
if appenders.hasMoreElements(), return; end

% Input check
import org.apache.log4j.Level;
levels = Level.getAllPossiblePriorities();
levels = arrayfun(@(x) char(x.toString()), levels, 'Unif', false);
ip=inputParser;
ip.addOptional('level', 'WARN', @(x) ismember(x, levels));
ip.parse(varargin{:});

% Configure log4j and set debug level
loci.common.DebugTools.enableLogging(ip.Results.level);