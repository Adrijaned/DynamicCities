/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.dynamicCities.parcels;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.dynamicCities.population.Culture;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.HashSet;
import java.util.Set;

@Share(CultureManager.class)
@RegisterPlugin
public class CultureManager extends BaseComponentSystem {
    private Logger logger = LoggerFactory.getLogger(Culture.class);
    private Set<Culture> cultures = new HashSet<>();
    private MersenneRandom rng;
    @In
    private AssetManager assetManager;

    @Override
    public void postBegin() {
        logger.info("Obtaining culture prefabs...");
        Set<Prefab> prefabs = assetManager.getLoadedAssets(Prefab.class);
        for (Prefab prefab : prefabs) {
            //Get building data
            if (prefab.hasComponent(Culture.class)) {
                Culture culture = prefab.getComponent(Culture.class);
                if (!culture.buildingNeedPerZone.isEmpty()) {
                    cultures.add(culture);
                } else {
                    logger.warn("Found culture prefab with empty buildingNeedPerZone list");
                }
            }
        }
        logger.info("Finished loading cultures: " + cultures.size() + " district types found: " + cultures.toString());
        rng = new MersenneRandom(assetManager.hashCode() * 5 + this.hashCode());
    }

    public Culture getRandomCulture ( ) {
        if (!cultures.isEmpty()) {
            int max = cultures.size();
            int index = rng.nextInt(max);
            return (Culture) cultures.toArray()[index];
        }
        logger.error("No culture found...barbarians..." );
        return null;
    }


}