-module(a).
-export([test/0]).

test() ->
	L = [{a, 3, 4}, {b, 5, 6}],
	case lists:keyfind(a, 1, L) of 
		{_, X, _} -> X;
		false -> 0
	end.
