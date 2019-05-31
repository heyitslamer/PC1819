-module(server).
-export([start/0, evento/1, waitDouble/1]).

get_timestamp() ->
  {Mega, Sec, Micro} = os:timestamp(),
  (Mega*1000000 + Sec)*1000 + round(Micro/1000).

start() ->
  register(?MODULE, spawn(fun() -> room(#{}) end)).

room(M) ->
  receive
    { _,  { event, Name } } ->
      V = maps:get(Name, M, []),
      [ W ! updt || W <- V ],
      room(M);
    { From, { waitD, Name } } ->
      V = maps:get(Name, M, []),
      L = [From|V],
      room(maps:put(Name, L, M));
    { From, { leave, Name } } ->
      V = maps:get(Name, M, []),
      L = V -- [From],
      room(maps:put(Name, L, M))
  end.

evento(N) ->
  ?MODULE ! { self(), { event, N } },
  ok.

waitDouble(N) ->
  ?MODULE ! { self(), { waitD, N } },
  receive
    updt -> waitDouble(N, get_timestamp())
  end.

waitDouble(N, T) ->
  receive
    updt ->
      V = get_timestamp(),
      if
        V - T > 100 -> 
          ?MODULE ! { self(), { leave, N } },
          ok;
        true -> 
          waitDouble(N, T)
      end
  end.



