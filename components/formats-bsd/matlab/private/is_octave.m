function is = is_octave ()
is = exist ('OCTAVE_VERSION', 'builtin') == 5;
end
