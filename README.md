# Ezturoyds Syndrome
Welcome to Ezturoyds Syndrome, the simple space combat game that is totally not an Asteroids Clone.

Notice: All commits on this repository were made by one person, but a bug makes them appear to be from different people.

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
