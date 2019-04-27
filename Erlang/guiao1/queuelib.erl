-module(queuelib).
-export([create/0, enqueue/2, dequeue/1]).

create() -> 
	{[], []}.

enqueue({P, W}, I) -> 
	{ [I | P], W }.

dequeue({P, W}) -> 
	case {P, W} of
		{[ ], [ ]} -> empty;
		{_, [C | Ca]} -> {{P, Ca}, C};
		{C, [ ]} -> dequeue({[ ], lists:reverse(C)})
	end.

