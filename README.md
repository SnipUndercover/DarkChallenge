# DarkChallenge

---

### What is this?
*A plugin that causes players to burn if exposed to sunlight, similar to zombies.*

The player will burn unless:
 - they're not in a compatible gamemode *(survival, adventure)*
 - they're not in the overworld
 - it is night
 - they're in the dark
 - there's a block above the player
 - the player is in the water
 - the player has a helmet on

Some of the details can be configured in `config.yml`.

### `config.yml` structure
```yml
# gamemodes in which the player should burn
affectedGamemodes:
  - survival
  - adventure

# dimensions in which the player should burn
affectedDimensions:
  - overworld

# the time in which the player can not burn
safeTime:
  from: 12500 # about sunset
  to: 23500 # about sunrise

# the length of the burn in ticks 
burnLength: 160 # 8 seconds

# helmet damaging data
helmetDamage:
  # the time to wait between damaging the player's helmet
  # scales with each level of unbreaking
  
  # no unbreaking:  80 ticks / 4  seconds
  # unbreaking 1 : 160 ticks / 8  seconds
  # unbreaking 2 : 240 ticks / 12 seconds
  # unbreaking 3 : 320 ticks / 16 seconds
  # ...
  waitTime: 80 # 4 seconds
  
  # whether to ignore unbreaking
  ignoreUnbreaking: false
```

### TODO
 - Add documentation
 - Add more tests
 - Rework `IgnitePlayersTask`
 - Try to make `plugin.yml` command declaration optional