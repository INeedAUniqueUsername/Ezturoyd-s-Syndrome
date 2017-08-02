# Super Nostalgia Entertainment Syndrome
## Retro gaming is a disease
Super Nostalgia Entertainment Syndrome (also known as SNES) is a condition where retro games survive long past their era and every depiction of video games involves retro games. It is especially prevalent in Hollywood with many cases of it having appeared in film and television. SNES also appears regularly in the independent video games industry.





## Ideas
- Add Cave Story-like collectible fragments for no apparent reason
- Keep track of damage to individual ship parts and have them break off into fragments
- More realistic turret turning
- Have weapons break off from damage
- Add ProjectileType and WeaponType classes that create objects based on parameters

## Class Hierarchy
- `SpaceObject`
- - `Asteroid`: Scrapped because I could not find an easy way to make them change shape from taking damage without looking excessively unrealistic
- - `Starship`: A `SpaceObject` that moves by turning, thrusting, and braking
- - - `Starship_NPC`: A `Starship` with a behavior controller. Can take "Orders"
- - `Projectile`:
- - - `Projectile_Tracking`
- `Weapon`: Creates projectiles
- `Behavior`: A scripted behavior module to control a `SpaceObject`
- - `BehaviorController_Default`: The central `Behavior` object of a `SpaceObjectt` that manages Orders. Currently works with only `Starship`s because I don't think `Asteroid`s need to act like living things
- - `Order_Escort`:
- - `Order_Attack`
- - `Order_AttackOrbit`
- - `Order_Hold`
- `Body`: Will be used to handle the graphics of a `SpaceObject`
- - `Body_Starship`
- - `Body_Projectile`
