package com.albionradar.data.model

/**
 * Resource state enumeration
 */
enum class ResourceState {
    /**
     * Resource is available for harvesting
     */
    AVAILABLE,
    
    /**
     * Resource has been depleted/harvested
     */
    DEPLETED,
    
    /**
     * Resource is being harvested by someone
     */
    IN_PROGRESS
}
